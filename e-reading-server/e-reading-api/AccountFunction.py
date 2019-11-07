from django.core.cache import cache
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger

from .MainFunction import lay_nguyen_mau_cua_tu
from .models import RequestHistory, User, UserExtend, VocabularyFavorite, Vocabulary
from .serializers import ListVocabularyFavoriteSerializer, ListHistoryNewFeedSerializer
# from .MainFunction import LEVEL_USER

def save_or_update_history_new_feed_by_url_new_feed(object_user_request, url_new_feed_request, word_request,
                                                    word_result, position_content):
    """
    Tạo mới một history hoặc update nó nếu đã tồn tại bản ghi.

    """
    try:
        object_history = RequestHistory.objects.get(user_id=object_user_request, url_new_feed=url_new_feed_request)

        if position_content == 1 and object_history.introduction_new_feed is None:
            object_history.introduction_new_feed = word_request

        if word_request in object_history.word:
            return
        word_request_old = object_history.word
        object_history.word = word_request_old + "\n" + word_request
        word_result_old = object_history.word_result
        object_history.word_result = word_result_old + "\n" + word_result
        object_history.save()
    except RequestHistory.DoesNotExist:
        RequestHistory(user_id=object_user_request, url_new_feed=url_new_feed_request,
                       title_new_feed=word_request, word=word_request, word_result=word_result).save()


def insert_or_update_level_user(id_user_set, level_set):
    """
    Set level của người dùng .
    :param id_user_set:
    :param level_set:
    :return:
    """
    user_extend = UserExtend.objects.filter(user_id=id_user_set)
    if user_extend.exists():
        user_extend.update(level_user=level_set)
        # print("Updated Level English.")
    else:
        user_set = User.objects.get(id=id_user_set)
        UserExtend(user_id=user_set, level_user=level_set).save()
        # print("Inserted Level English.")


def list_level_default_user():
    """
    get list level of user
    :return:
    """
    return [{"name": "Không có kiến ​​thức về tiếng Anh",  # Không có kiến ​​thức về tiếng anh : No knowledge of English
             "value": 630072115},
            {"name": "Trình độ tiếng Anh tiểu học",  # Trình độ tiếng Anh tiểu học : Elementary level of English
             "value": 80893690},
            {"name": "Trình độ tiếng Anh trung cấp thấp",
             # Trình độ tiếng anh trung cấp thấp : Low intermediate level of English
             "value": 26708670},
            {"name": "Trình độ tiếng Anh trung cấp cao",
             # Trình độ tiếng Anh trung cấp cao : High intermediate level of English
             "value": 11315623},
            {"name": "Trình độ tiếng Anh nâng cao",  # Trình độ tiếng anh nâng cao : Advanced level of English
             "value": 5396579},
            {"name": "Tiếng Anh thành thạo",  # Tiếng Anh thành thạo : Proficient in English
             "value": 2917632}]

    # return [{"name": "Không có kiến ​​thức về tiếng Anh",  # Không có kiến ​​thức về tiếng anh : No knowledge of English
    #          "value": LEVEL_USER[0]},
    #         {"name": "Trình độ tiếng Anh tiểu học",  # Trình độ tiếng Anh tiểu học : Elementary level of English
    #          "value": LEVEL_USER[1]},
    #         {"name": "Trình độ tiếng Anh trung cấp thấp",
    #          # Trình độ tiếng anh trung cấp thấp : Low intermediate level of English
    #          "value": LEVEL_USER[2]},
    #         {"name": "Trình độ tiếng Anh trung cấp cao",
    #          # Trình độ tiếng Anh trung cấp cao : High intermediate level of English
    #          "value": LEVEL_USER[3]},
    #         {"name": "Trình độ tiếng Anh nâng cao",  # Trình độ tiếng anh nâng cao : Advanced level of English
    #          "value": LEVEL_USER[4]},
    #         {"name": "Tiếng Anh thành thạo",  # Tiếng Anh thành thạo : Proficient in English
    #          "value": LEVEL_USER[5]}]


def get_position_of_level_user_by_id_user(id_user_request):
    level_value = UserExtend.objects.filter(user_id__id=id_user_request).first()
    if level_value is not None:
        for index, item in enumerate(list_level_default_user()):
            if level_value.level_user in list(item.values()):
                return index
    return -1


def get_value_of_level_user_by_level_name(name_level):
    """
        Get level of user by name_level
    :param name_level:
    :return:
    """
    level = list_level_default_user()[0]["value"]  # Set level default nếu không tồn tại
    for value in list_level_default_user():
        if value["name"] == name_level:
            level = value["value"]
    return level


def get_value_of_level_user_by_position(position_level):
    """
        Get level of user by position_level
    :param position_level:
    :return:
    """
    try:
        return list_level_default_user()[int(position_level)]["value"]
    except IndexError as e:
        print("Error:{}".format(e))
        return list_level_default_user()[0]["value"]


def get_size_list_level():
    """
    get size of level
    :return:
    """
    return len(list_level_default_user())


def get_dict_of_level_user_by_position(position_level):
    """
    Get dict of user by position_level
    :param position_level:
    :return:
    """
    try:
        return list_level_default_user()[int(position_level)]
    except IndexError as e:
        print("Error:{}".format(e))
        return list_level_default_user()[0]


def check_name_level_is_correct(name_level):
    for value in list_level_default_user():
        if value["name"] == name_level:
            return True
    else:
        return False


def add_vocabulary_to_dairy_by_id(object_user_request, object_vocabulary_request, type_vocabulary_request,
                                  is_hard=True):
    if object_user_request is not None and object_vocabulary_request is not None:
        cache.clear()  # Clear cache khi dich lai.
        return VocabularyFavorite(user_id=object_user_request, vocabulary_id=object_vocabulary_request,
                                  type_vocabulary=type_vocabulary_request, is_hard=is_hard).save()


def add_vocabulary_to_dairy_by_word(id_user, word_request, is_hard=True):
    user_set = User.objects.get(id=id_user)
    try:
        word_request = lay_nguyen_mau_cua_tu(word_request)
        print(word_request)
        vocabulary_set = Vocabulary.objects.filter(word=word_request).first()
        VocabularyFavorite(user_id=user_set, vocabulary_id=vocabulary_set, is_hard=is_hard).save()
    # print("Inserted VocabularyFavorite English.")
    except Vocabulary.DoesNotExist:
        print("{} Does not exits on DB".format(word_request))


PAGE_LIMIT_RESULT = 15


def get_dairy(id_user, page_request):
    user_set = User.objects.get(id=id_user)
    list_object = VocabularyFavorite.objects.filter(user_id=user_set).order_by('-time_add')
    paginator = Paginator(list_object, PAGE_LIMIT_RESULT)

    try:
        objects = paginator.page(page_request)
    except PageNotAnInteger:
        objects = paginator.page(page_request)
    except EmptyPage:
        objects = paginator.page(paginator.num_pages)

    serializer = ListVocabularyFavoriteSerializer(objects, many=True)
    return {
        'result_count': list_object.count(),
        'page': objects.number,
        'next_page_flg': objects.has_next(),
        'result': serializer.data,
    }


def get_level_value_by_user_id(id_user_request):
    user_info = UserExtend.objects.filter(user_id=id_user_request).first()
    if user_info is None:
        return -1
    return user_info.level_user


def delete_favorite_by_id_favorite(id_user_request, id_favorite_request):
    cache.clear()  # Clear cache khi dich lai.
    return VocabularyFavorite.objects.filter(id=id_favorite_request, user_id=id_user_request).delete()


def get_history_new_feed_by_object_user(object_user_request, page_request):
    if object_user_request is not None:
        list_object = RequestHistory.objects.filter(user_id=object_user_request).order_by('-time_request')
        paginator = Paginator(list_object, PAGE_LIMIT_RESULT)
        try:
            objects = paginator.page(page_request)
        except PageNotAnInteger:
            objects = paginator.page(page_request)
        except EmptyPage:
            objects = paginator.page(paginator.num_pages)
        serializer = ListHistoryNewFeedSerializer(objects, many=True)
        return {
            'result_count': list_object.count(),
            'page': objects.number,
            'next_page_flg': objects.has_next(),
            'result': serializer.data,
        }


def get_level_name_by_value(value_level):
    for line in list_level_default_user():
        if value_level == line["value"]:
            return line["name"]
