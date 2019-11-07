from nltk import word_tokenize, pos_tag, download
from nltk.stem.snowball import SnowballStemmer

"""
    Dowmload nltk_data data of nltk
"""
download('stopwords')
download('punkt')
download('averaged_perceptron_tagger')
download('universal_tagset')

from .models import Vocabulary, VocabularyType, VocabularyFavorite
from django.core.cache import cache
from threading import Thread

LEN_SPACE = 1
TIME_OUT_CACHE = 60 * 30  # thời gian tồn tại của cache; đơn vị giây


def is_should_translator(vocabulary_request, level_of_user_request, object_user_request):
    """
        Xét xem từ đó có cần dịch hay không
        Trả về điều kiện kèm theo từ đó
    """
    vocabulary_of_db = try_to_find_word_on_db(vocabulary_request)

    if vocabulary_of_db is not None:
        if vocabulary_of_db.popularity <= level_of_user_request:
            return True, vocabulary_of_db  # Case tồn tài từ trong DB và là từ phổ biến

        if object_user_request is not None:
            vocabulary_favorite = VocabularyFavorite.objects.filter(user_id=object_user_request,
                                                                    vocabulary_id=vocabulary_of_db.id).first()
            if vocabulary_favorite is not None and vocabulary_favorite.is_hard:
                return True, vocabulary_of_db  # Case tồn tài từ trong DB không phải từ phổ biến nhưng là từ khó của user
        return False, vocabulary_of_db  # Case tồn tại từ trong DB nhưng không cần dịch
    return False, None  # Case không tồn tài từ trong DB


# Xếp hạng từ trên xuống tương ững với basic đến pro
LEVEL_USER_1 = 630072115
LEVEL_USER_2 = 80893690
LEVEL_USER_3 = 26708670
LEVEL_USER_4 = 11315623
LEVEL_USER_5 = 5396579
LEVEL_USER_6 = 2917632

# LEVEL_USER = [
#     0,
#     16000,
#     32000,
#     48000,
#     64000,
#     80000
# ]

# LEVEL_USER_1 = 0
# LEVEL_USER_2 = 16000
# LEVEL_USER_3 = 32000
# LEVEL_USER_4 = 48000
# LEVEL_USER_5 = 64000
# LEVEL_USER_6 = 80000

LEVEL_USER_DEFAULT = LEVEL_USER_1
ID_USER_DEFAULT = 0


def detect_work_by_nltk(text, levelsOfUser, object_user_request):
    """
        Dịch các từ theo độ khó và độ phổ biến
        Trả về đoạn text có kèm các từ đã dịch trong ngoặc
    """

    # Chuyển đoãn text thành list các từ
    list_text = word_tokenize(text)

    # Gán nhãn xác định loại tiwf
    list_tag_vocabulary = pos_tag(list_text, tagset='universal')

    # Danh sách trả về
    list_vocabulary_result = []
    list_word_none_have_translate = []

    count_index_words_changed = 0  # vị trí của từ sau khi chèn từ mới vào
    position_words_changed = 0
    for idx, val in enumerate(list_tag_vocabulary):
        vocabulary = str(val[0]).lower()
        len_words = len(vocabulary) + LEN_SPACE
        len_new_words = 0
        type_vocabulary = val[1].strip()

        if vocabulary in ",.()'\"-+=!?><;:{}[]~@#$%^&*":  # if tag is not alpha then skip
            # print("Phát hiện kí tự: ", vocabulary)
            position_words_changed += len_words
            continue

        is_translate, vocabulary_of_db = is_should_translator(vocabulary, level_of_user_request=levelsOfUser,
                                                              object_user_request=object_user_request)

        if is_translate and vocabulary_of_db is not None:  # Check xem từ đó có nên dịch không
            mean_word_of_db = VocabularyType.objects.filter(
                id_vocabulary=vocabulary_of_db.id,
                type_vocabulary=type_vocabulary).first()

            if mean_word_of_db is not None:
                count_index_words_changed += 1
                mean_short_word_of_db = mean_word_of_db.mean_short

                list_vocabulary_result.append(
                    vocabulary_result(vocabulary_of_db.id,
                                      mean_word_of_db.type_vocabulary,
                                      vocabulary_of_db.word, position_words_changed,
                                      position_words_changed + len(vocabulary)))
                new_words = "({})".format(mean_short_word_of_db)
                len_new_words = len(new_words) + LEN_SPACE
                list_text.insert(idx + count_index_words_changed, new_words)
        elif vocabulary_of_db is not None:
            list_word_none_have_translate.append(
                vocabulary_result_none_translate(vocabulary_of_db.id, type_vocabulary, vocabulary,
                                                 position_words_changed,
                                                 position_words_changed + len(vocabulary))
            )
        position_words_changed += len_words + len_new_words

    return " ".join(list_text), list_vocabulary_result, list_word_none_have_translate


def vocabulary_result(id, type_word, word, start_index, end_index):  # Model word result
    return {"id": int(id), "word": str(word), "type": str(type_word), "start_index": start_index,
            "end_index": end_index}


def vocabulary_result_none_translate(id, type_word, word, start_index, end_index):  # Model word result
    return {"id": int(id), "word": str(word), "type": str(type_word), "start_index": start_index,
            "end_index": end_index}


def save_or_get_new_feed_from_cache(words_translate, level, object_user_request, url_source_feed_request,
                                    position_content_request):
    id_user_request = ID_USER_DEFAULT
    level_request = LEVEL_USER_DEFAULT

    if object_user_request is not None:
        id_user_request = object_user_request.id
        level_request = level

    key_cache = "{};{};{};{}".format(id_user_request, level_request, position_content_request, url_source_feed_request)
    print("key_cache", key_cache)

    if not cache.has_key(key_cache, version=position_content_request):
        a, b, c = detect_work_by_nltk(words_translate, level_request, object_user_request)
        Thread(target=cache.add, args=(key_cache, (a, b, c), TIME_OUT_CACHE, position_content_request)).start()
    else:
        a, b, c = cache.get(key_cache, version=position_content_request)
    cache.close()
    return a, b, c


def refresh_word_feed_from_cache(level, object_user_request, word_request, url_source_feed_request,
                                 position_content_request):
    id_user_request = ID_USER_DEFAULT
    level_request = LEVEL_USER_DEFAULT

    if object_user_request is not None:
        id_user_request = object_user_request.id
        level_request = level
    key_cache = "{};{};{};{}".format(id_user_request, level_request, position_content_request, url_source_feed_request)
    print(cache.clear())  # Clear cache khi dich lai.
    a, b, c = detect_work_by_nltk(word_request, level, object_user_request)
    # if cache.has_key(key_cache, version=position_content_request):  # Cập nhật lại cache
    #     Thread(target=cache.set, args=(key_cache, (a, b, c), TIME_OUT_CACHE, position_content_request)).start()

    return a, b, c


def lay_nguyen_mau_cua_tu(word):
    return SnowballStemmer("english").stem(word)


def try_to_find_word_on_db(word_request):
    """
    Tim một từ trong DB và cố gắng đưa về dạng nguyên mẫu để tìm nó
    :param word_request:
    :return:
    """
    word_request = word_request.lower()  # Đưa về dạng thống nhất để duyệt
    vocabulary_of_db = Vocabulary.objects.filter(word=word_request).first()
    if vocabulary_of_db is None:
        word_request = lay_nguyen_mau_cua_tu(word_request)
        return Vocabulary.objects.filter(
            word=word_request).first()  # Trường hợp tôi không tìm thấy trong DB tôi sẽ cố gắng đưa từ đó về dạng nguyên mẫu
    else:
        return vocabulary_of_db
