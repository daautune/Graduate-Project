import psycopg2
import datetime


def initListVovabularyFromFile(urlFile):
    """
        Load  điển anh việt từ file 
    """

    file = open(urlFile, "r+")
    data = file.read().strip()
    file.close()
    arrayData = data.split("@")
    return arrayData, len(arrayData)


host = "127.0.0.1"
port = "5432"
database = "default"
user = "capstone2"
password = "capstone2"


def insertDataFromFileToDB():
    """
        Insert Data từ file vào databse
    """

    listVocabulary, sizeList = initListVovabularyFromFile(
        "./e-reading-api/language_process/anhviet109K.txt")

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
                    key = "NOUN"
                    insertVocabularyType(value, key, idInserted)
                    #Lọc được thì thoát ra luôn
                    break

        # Print progress
        print("{} / {} :Vocabulary".format(count, sizeList), end='\r')

    writeFileLogError("Insert Data From File To DB. Done!!!!!!!!!!!", True)
    writeFileLogError("Có tổng là {} từ vựng được thêm vào DB".format(count),
                      True)


def insertVocabulary(line):
    conn = psycopg2.connect(
        host=host, port=port, database=database, user=user, password=password)

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
                VALUES(%s,%s,0) RETURNING id;"""

        cur = conn.cursor()

        cur.execute(sql, (
            str(word).strip(),
            str(phienAm).strip(),
        ))

        idVocavulary = cur.fetchone()[0]

        conn.commit()
        # close communication with the database
        cur.close()
    except (Exception, psycopg2.DatabaseError) as error:
        writeFileLogError(error)
    finally:
        conn.close()

    return idVocavulary


def insertVocabularyType(lineType, type_vocabulary, id_vocabulary):
    conn = psycopg2.connect(
        host=host, port=port, database=database, user=user, password=password)

    idVocavularyType = -1

    mean_short = "None"
    for line in lineType.splitlines():
        if "-" in line:
            mean_short = line.split(";")[0]
            mean_short = str(mean_short).replace("-", "").strip()
            break

    try:
        sql = """INSERT INTO tbl_vocabulary_type(id_vocabulary,type_vocabulary,mean_short,mean,example)
                VALUES(%s,%s,%s,%s,%s) RETURNING id;"""

        cur = conn.cursor()

        cur.execute(sql, (
            id_vocabulary,
            type_vocabulary.strip(),
            mean_short.strip(),
            lineType.strip(),
            "",
        ))

        idVocavularyType = cur.fetchone()[0]

        conn.commit()
        # close communication with the database
        cur.close()
    except (Exception, psycopg2.DatabaseError) as error:
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

    # print(error)


# Main procress
insertDataFromFileToDB()
