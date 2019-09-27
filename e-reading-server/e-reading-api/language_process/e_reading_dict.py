from nltk import word_tokenize, pos_tag, download
from nltk.stem.snowball import SnowballStemmer
"""
    Dowmload nltk_data data of nltk
"""
download('stopwords')
download('punkt')
download('averaged_perceptron_tagger')
download('universal_tagset')


def initDictVovabularyFromFile(urlFile):
    """
        Load từ điển anh việt từ file 
    """

    file = open(urlFile, "r+", encoding='utf8')
    data = file.read().strip()
    file.close()
    arrayData = data.split("@")
    myDict = {}
    for i in arrayData:
        try:
            word = i.splitlines()[0].split(" /")[0]
            myDict[word] = i
        except:
            pass
    printProgress("init Dict Vocabulary Success", len(myDict))
    return myDict


def initDictCommonWorkFromFile(urlFile):
    """
        Load danh sách độ phổ biến của các từ tiếng anh 
    """

    file = open(urlFile, "r+", encoding='utf8')
    data = file.read().strip()
    file.close()
    arrayData = data.splitlines()

    myDict = {}
    for i in arrayData:
        work = i.split("\t")
        myDict[work[0].lower()] = work[1]
    printProgress("init Dict Common Work Success", len(myDict))
    return myDict


def printProgress(comment, bodyComment, line="="):
    print(line * 10, comment, line * 10, "\n", bodyComment)


printProgress("Initing data ", 'In Progress')

dictVocabulary = initDictVovabularyFromFile(
    "/home/E-Reading-server/e-reading-api/language_process/anhviet109K.txt")

dictDataCommon = initDictCommonWorkFromFile(
    "/home/E-Reading-server/e-reading-api/language_process/google-books-common-words.txt"
)

printProgress("Initing data ", 'Success')


def isShouldTranslator(vocabulary,
                       dictDataCommon,
                       level,
                       isHardVocabulary=False):

    if isHardVocabulary:
        return True
    if vocabulary in dictDataCommon:
        if int(dictDataCommon[vocabulary]) <= level:
            return True

    return False


def convertWork(text):
    """
        Dịch các từ theo độ khó và độ phổ biến
        Trả về đoạn text có kèm các từ đã dịch trong ngoặc
    """

    dictType = intDictType()

    # Chuyển đoãn text thành list các từ
    listText = word_tokenize(text)
    printProgress("Phân tách các từ", "listText")

    listTagVocabulary = pos_tag(listText, tagset='universal')
    printProgress("Gán nhãn cho từ", 'listTagVocabulary')

    count = 0
    for idx, val in enumerate(listTagVocabulary):
        vocabulary = val[0]
        typeVocabulary = val[1]

        try:
            if typeVocabulary not in ",.":
                typeVocabulary = dictType[typeVocabulary]

        except KeyError as e:
            print("Không tìm thấy loại từ : ", str(e))
            typeVocabulary = dictType["NOUN"]

        if isShouldTranslator(vocabulary, dictDataCommon, level=35069579):
            try:
                meanWork = dictVocabulary[vocabulary]
                meanWork = meanVocabularyByType(meanWork, typeVocabulary)

            except KeyError as e:

                try:
                    meanWork = SnowballStemmer("english").stem(vocabulary)
                    meanWork = dictVocabulary[meanWork]
                    meanWork = meanVocabularyByType(meanWork, typeVocabulary)

                except KeyError as e:
                    print("Không tìm thấy từ trong từ điển: ", vocabulary)
                    meanWork = vocabulary

            count = count + 1
            listText.insert(idx + count, "({})".format(meanWork))

    return " ".join(listText)


def intDictType():
    """
        Mapping kiểu của từ vựng 
    """

    return {
        "NOUN": "danh từ",
        "VERB": "động từ",
        "ADJ": "tính từ",
        "ADV": "trạng từ",
        "PRT": "giới từ",
        "DET": "mạo từ",
        "ADP": "giới từ",
        "PRON": "đại từ",
    }


def meanVocabularyByType(vocabulary, typeVocabulary):
    """
        Tìm nghĩa của một vừng theo loại của từ đó
    """

    for line in vocabulary.split("* "):
        if typeVocabulary in line:
            for line in line.splitlines():
                if "-" in line:
                    mean = line.split(";")[0]
                    return str(mean).replace("-", "").strip()
        else:
            for line in line.splitlines():
                if "-" in line:
                    mean = line.split(";")[0]
                    return str(mean).replace("-", "").strip()
