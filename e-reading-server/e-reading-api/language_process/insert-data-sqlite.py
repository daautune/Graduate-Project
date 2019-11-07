import datetime
import sqlite3
from nltk.stem.snowball import SnowballStemmer


def initListVovabularyFromFile(urlFile):
    """
        Load từ điển anh việt từ file 
    """

    file = open(urlFile, "r+")
    data = file.read().strip()
    file.close()
    arrayData = data.split("@")
    return arrayData, len(arrayData)


def insertDataFromFileToDB():
    """
        Insert Data từ file vào databse
    """

    file_name = "./e-reading-api/language_process/anhviet109K.txt"
    # file_name = "./e-reading-api/language_process/new-dict.txt"

    listVocabulary, sizeList = initListVovabularyFromFile(file_name)

    writeFileLogError(
        "Insert Data From File To DB. In Progress.... {}".format(sizeList),
        True, True)

    dictType = {
        "NOUN": "danh từ",
        "VERB": "động từ",
        "ADJ": "tính từ",
        "ADV": "trạng từ",
        "DET": "mạo từ",
        "ADP": "giới từ",
        "PRON": "đại từ",
        "CONJ": "liên từ",
        "PRT": "phó từ",
        # "NUM": "số",  #Hien tai trong tu dien so dang la danh tu
    }

    count = 0
    for voc in listVocabulary:
        count = count + 1
        idInserted = -1
        for idx, value in enumerate(voc.splitlines()):
            if idx == 0:
                idInserted = insertVocabulary(value)
                voc = voc.replace(value, "")
                break

        if idInserted != -1:
            for value in voc.split("* "):
                #Lọc ra loại từ trong value này
                for key, val in dictType.items():
                    if val in value:
                        if "ngoại động từ" in value:
                            key = "VERB.T"
                        #Trong App này mặc định dùng nội động từ để dịch nên bỏ qua bược này
                        # elif "nội động từ" in value:
                        #     key = "VERB.I"

                        insertVocabularyType(value, key, idInserted)
                        #Lọc được thì thoát ra luôn
                        break

                    # Trường hợp một từ không có Type nào cả
                    # key = "NOUN"
                    # insertVocabularyType(value, key, idInserted)
                    # Lọc được thì thoát ra luôn
                    # break

        # Print progress
        print("{} / {} :Vocabulary".format(count, sizeList), end='\r')

    writeFileLogError("Insert Data From File To DB. Done!!!!!!!!!!!", True)
    writeFileLogError("Có tổng là {} từ vựng được thêm vào DB".format(count),
                      True)


def insertVocabulary(line):
    conn = sqlite3.connect('db.sqlite3')

    idVocavulary = -1
    word = ""
    phienAm = ""

    try:
        line = line.split("/")
        word = line[0]
        phienAm = line[1]
    except IndexError:  #Trường hợp từ đó không có phiên âm
        writeFileLogError("Từ {} không có phiên âm \n".format(word))
        word = line[0]
        phienAm = ""

    try:
        sql = """INSERT INTO tbl_vocabulary(word,pronucation,popularity)
                VALUES(?,?,0) """

        cur = conn.cursor()

        cur.execute(sql, (
            str(word).strip(),
            str(phienAm).strip(),
        ))

        idVocavulary = cur.lastrowid

        conn.commit()
        # close communication with the database
        cur.close()
    except (Exception, sqlite3.DatabaseError) as error:
        writeFileLogError(error)
    finally:
        conn.close()

    return idVocavulary


def insertVocabularyType(lineType, type_vocabulary, id_vocabulary):
    conn = sqlite3.connect('db.sqlite3')

    idVocavularyType = -1

    mean_short = "None"
    for line in lineType.splitlines():
        if "-" in line:
            mean_short = line.split(";")[0]
            mean_short = str(mean_short).replace("-", "").strip()
            break

    try:
        sql = """INSERT INTO tbl_vocabulary_type(id_vocabulary,type_vocabulary,mean_short,mean,example)
                VALUES(?,?,?,?,?) """

        cur = conn.cursor()

        cur.execute(sql, (
            id_vocabulary,
            type_vocabulary.strip(),
            mean_short.strip(),
            lineType.strip(),
            "",
        ))

        idVocavularyType = cur.lastrowid

        conn.commit()
        # close communication with the database
        cur.close()
    except (Exception, sqlite3.DatabaseError) as error:
        writeFileLogError(error)
    finally:
        conn.close()

    return idVocavularyType


def writeFileLogError(error, isLogDateTime=False, isFirstLog=False):
    if isFirstLog:
        typeWrite = "w"
    else:
        typeWrite = "a"

    if isLogDateTime:
        string = "{} : {} \n".format(datetime.datetime.now(), str(error))
    else:
        string = "{} \n".format(str(error))

    logFile = open("LogInsertDatabase.txt", typeWrite)
    logFile.write(string)
    logFile.close()

    print(error)


def writeFileLog(error, isLogDateTime=False, isFirstLog=False):
    if isFirstLog:
        typeWrite = "w"
    else:
        typeWrite = "a"

    if isLogDateTime:
        string = "{} : {} \n".format(datetime.datetime.now(), str(error))
    else:
        string = "{} \n".format(str(error))

    logFile = open("LogUpdatePopularity.txt", typeWrite)
    logFile.write(string)
    logFile.close()

    print(error)


def updatePopularityOfVocabulary():

    # dictDataCommon, sizeDict = initDictCommonWorkFromFile(
    #     "./e-reading-api/language_process/common-words.txt")

    # dictDataCommon, sizeDict = initDictCommonWorkFromFile(
    #     "./e-reading-api/language_process/common-words.txt")

    dictDataCommon, sizeDict = initDictCommonWorkFromFile(
        "./e-reading-api/language_process/google-books-common-words.txt")

    writeFileLog(
        error="Start Insert with size of {}".format(sizeDict),
        isLogDateTime=True,
        isFirstLog=True)

    count = 0
    countMissing = 0
    progress = 0
    for key, value in dictDataCommon.items():
        tempCount = 0
        tempCount = updateVocabularyDB(word=key, popularity=value)
        if tempCount == 0:
            nguyenMau = SnowballStemmer("english").stem(key)
            tempCount = updateVocabularyDB(word=nguyenMau, popularity=value)

            if tempCount == 0:  # Sau khi dùng thư viện vẫn không đưa về đúng nguyên mẫu
                lenWord = len(key)
                wordSub = key

                while lenWord >= len(key) - 2:  # Cắt từ phía phải về trái 2 lần để tìm từ nguyên mẫu chính xác
                    wordSub = wordSub[:lenWord]

                    tempCount = updateVocabularyDB(
                        word=wordSub, popularity=value)
                    if tempCount > 0:  # nếu đã có từ đó trong DB thì thành công và thoát ra
                        break

                    lenWord = lenWord - 1
                    # Kết thúc mà vẫn khôgn tim được

                if tempCount == 0:  # Các từ bị miss vì ko tìm thấy trong DB vì bất cứ lí do gì
                    writeFileLog(
                        error="Khong co trong tu dien:(Dạng Thì :: Dạng ng Mẫu ) {}::{} "
                        .format(key, wordSub)
                    )
                    countMissing = countMissing + 1

        count = count + tempCount
        progress = progress + 1

        # Print progress
        print(
            "Progress: {} / {}; Vocabulary Updated : {}".format(
                progress, sizeDict, count),
            end='\r')

    # Ghi lại kết quả
    writeFileLog(
        error="Done with Total: {}, Success: {}, Missing: {}".format(
            sizeDict, count, countMissing), isLogDateTime=True)


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
        work = i.split()
        myDict[work[0].lower()] = work[1]
    return myDict, len(myDict)


def updateVocabularyDB(word, popularity):
    conn = sqlite3.connect('db.sqlite3')

    try:
        sql = """UPDATE tbl_vocabulary
              SET popularity = ? 
              WHERE word = ? AND popularity = 0"""
              

        cur = conn.cursor()

        cur.execute(sql, (
            popularity,
            word.lower(),
        ))

        updated = cur.rowcount

        conn.commit()
        # close communication with the database
        cur.close()
    except sqlite3.DatabaseError as error:
        print(error)
    finally:
        conn.close()

    return updated


# Main procress

# In sert vocabulary tu file vao tu dien
# insertDataFromFileToDB()# Done

# Cập nhật độ phổ biến của các từ
updatePopularityOfVocabulary()
