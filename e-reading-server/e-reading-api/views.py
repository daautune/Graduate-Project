import json
from threading import Thread

from django.contrib.auth import authenticate
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status, generics, permissions
from rest_framework.authtoken.models import Token
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.decorators import api_view, APIView, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response

from .AccountFunction import list_level_default_user, insert_or_update_level_user, \
    get_value_of_level_user_by_level_name, get_value_of_level_user_by_position, get_size_list_level, \
    get_dict_of_level_user_by_position, check_name_level_is_correct, add_vocabulary_to_dairy_by_id, get_dairy, \
    get_level_value_by_user_id, get_position_of_level_user_by_id_user, delete_favorite_by_id_favorite, \
    save_or_update_history_new_feed_by_url_new_feed, get_history_new_feed_by_object_user, get_level_name_by_value
from .MainFunction import save_or_get_new_feed_from_cache, detect_work_by_nltk, refresh_word_feed_from_cache
from .models import User, Vocabulary, UserExtend
from .serializers import UserRegistrationSerializer

# Create your views here.

BODY_WORD_REQUEST = 'words'
BODY_URL_SOURCE_FEED_REQUEST = 'url_source_feed'
BODY_POSITION_CONTENT_FEED_REQUEST = 'position_content'
BODY_LIST_VOCABULARY_REQUEST = 'vocabularies'
BODY_DECODE = 'utf-8'
BODY_DATA_REQUEST = 'data'


def index(request):
    return HttpResponse(
        'Welcome to Api for English Reading index', status=status.HTTP_200_OK)


class UserView(APIView):
    def post(self, request):
        try:
            Token.objects.get(user_id=request.user.id).delete()
        except Token.DoesNotExist:
            return form_json_require_login()

        return Response({'detail': "Logout success."},
                        status=status.HTTP_200_OK)


class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(
            data=request.data, context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)

        return Response({
            'token': token.key,
            'user_id': user.pk,
            'email': user.email
        })


@csrf_exempt
@api_view(["POST"])
@permission_classes((AllowAny,))
def login(request):
    username = request.data.get("username")
    password = request.data.get("password")
    if username is "" and password is "":
        return Response({'username': ['Tài khoản không được để trống.'],
                         'password': ['Mật khẩu không được để trống.']},
                        status=status.HTTP_400_BAD_REQUEST)
    if username is "":
        return Response({'username': ['Tài khoản không được để trống.']},
                        status=status.HTTP_400_BAD_REQUEST)
    if password is "":
        return Response({'password': ['Mật khẩu không được để trống.']},
                        status=status.HTTP_400_BAD_REQUEST)
    user = authenticate(username=username, password=password)
    if not user:
        return Response({'username': ['Tài khoản hoặc mât khẩu nhập vào không chính xác.'],
                         'password': ['Tài khoản hoặc mât khẩu nhập vào không chính xác.']},
                        status=status.HTTP_400_BAD_REQUEST)
    token, _ = Token.objects.get_or_create(user=user)

    level_english = None
    try:
        user_info = UserExtend.objects.get(user_id=user)
        level_english = user_info.level_user
        level_english = get_level_name_by_value(level_english)
    except UserExtend.DoesNotExist:
        pass

    return Response({
        'token': token.key,
        'user_id': user.pk,
        'email': user.email,
        'level_english': level_english
    }, status=status.HTTP_200_OK)


class UserRegistrationAPIView(generics.CreateAPIView):
    """
    Method is ghi đề api register của framework 
    """
    permission_classes = (permissions.AllowAny,)
    serializer_class = UserRegistrationSerializer


@api_view(['POST'])
def api_convert_work(request):
    """
    Api detect word and translate words
    :param request:
    :return:
    """

    body_unicode = request.body.decode(BODY_DECODE)
    body = json.loads(body_unicode)
    try:
        words_translate = body[BODY_WORD_REQUEST]
    except KeyError as e:
        print(e)
        return Response({'detail': "Body not correct {'%s':'String'}" % BODY_WORD_REQUEST},
                        status=status.HTTP_400_BAD_REQUEST)
    if request.user.is_authenticated:
        id_user_request = request.user.id
        object_user_request = User.objects.get(id=id_user_request)

        level_name_request = request.query_params.get('level_name')
        if level_name_request is None:
            return Response({'detail': "Parameter not correct[level_name : String]."},
                            status=status.HTTP_400_BAD_REQUEST)
        elif not check_name_level_is_correct(level_name_request):
            return Response({'detail': "Parameter level_name not Correct."},
                            status=status.HTTP_400_BAD_REQUEST)

        level_value = get_value_of_level_user_by_level_name(level_name_request)
        text_result, words_result, words_result_none_translate = detect_work_by_nltk(
            words_translate, level_value, object_user_request)

    else:
        level_value_default = get_value_of_level_user_by_level_name(None)
        text_result, words_result, words_result_none_translate = detect_work_by_nltk(
            words_translate, level_value_default, None)

    return JsonResponse({
        "text": text_result,
        "listWords": words_result,
        "list_word_not_translate": words_result_none_translate
    },
        status=status.HTTP_200_OK)


class LevelOfUser(APIView):
    def get(self, request):
        """
        Api get list value,name of level
        :param request:
        :return:
        """
        if request.user.is_authenticated:
            return JsonResponse({'levels': list_level_default_user(),
                                 'level_selected': get_position_of_level_user_by_id_user(request.user.id)
                                 }, status=status.HTTP_200_OK)
        else:
            return form_json_require_login()

    def post(self, request):
        """
        Api Get list level of user
        :param request:
        :return:
        """
        if request.user.is_authenticated:
            level_position = request.query_params.get('level_position')
            size_list_level = get_size_list_level() - 1
            if level_position is not None and str(level_position).isnumeric() and (
                    0 <= int(level_position) <= size_list_level):
                id_user = request.user.id
                level_value = get_value_of_level_user_by_position(level_position)
                insert_or_update_level_user(id_user_set=id_user, level_set=level_value)
                return JsonResponse({'level_user': get_dict_of_level_user_by_position(level_position)},
                                    status=status.HTTP_200_OK)
            else:
                return Response(
                    {'detail': "Parameter not correct[level_position : Int[0-{}]].".format(size_list_level)},
                    status=status.HTTP_400_BAD_REQUEST)
        else:
            return form_json_require_login()


class UserVocabularyFavorite(APIView):
    def get(self, request):
        """
        Get list vocabulary of favorite
        :param request:
        :return:
        """
        if request.user.is_authenticated:
            page_request = request.GET.get('page', 1)
            return Response(get_dairy(request.user.id, page_request), status=status.HTTP_200_OK)
        else:
            return form_json_require_login()

    def post(self, request):
        if request.user.is_authenticated:
            body_unicode = request.body.decode(BODY_DECODE)
            body = json.loads(body_unicode)
            try:
                data = body[BODY_DATA_REQUEST]
            except KeyError as e:
                print(e)
                return Response({'detail': "Body not correct {'%s':'String'}" % BODY_DATA_REQUEST},
                                status=status.HTTP_400_BAD_REQUEST)

            count = 0
            for vocabulary in data:
                object_vocabulary = Vocabulary.objects.get(id=vocabulary['id'])
                object_user_request = User.objects.get(id=request.user.id)
                inserted, _ = add_vocabulary_to_dairy_by_id(object_user_request, object_vocabulary,
                                                            vocabulary['type'])
                count += inserted
            return Response({'detail': count}, status=status.HTTP_200_OK)
        else:
            return form_json_require_login()


def form_json_require_login():
    return JsonResponse({'detail': "Trước tiên bạn phải đăng nhập."}, status=status.HTTP_401_UNAUTHORIZED)


@api_view(['POST'])
def translate_new_feed(request):
    """
    Api detect word and translate words
    :param request:
    :return:
    """

    body_unicode = request.body.decode(BODY_DECODE)
    body = json.loads(body_unicode)
    try:
        word_request = body[BODY_WORD_REQUEST]
        url_source_feed = body[BODY_URL_SOURCE_FEED_REQUEST]
        position_content = body[BODY_POSITION_CONTENT_FEED_REQUEST]
    except KeyError as e:
        print(e)
        return Response({'detail': "Body not correct {'%s':'String'}" % BODY_WORD_REQUEST},
                        status=status.HTTP_400_BAD_REQUEST)
    
    if request.user.is_authenticated:
        id_user_request = request.user.id
        object_user_request = User.objects.get(id=id_user_request)

        level_value = get_level_value_by_user_id(id_user_request)
        print("Level", level_value)

        if level_value == -1:
            return Response({'detail': "Trước tiên hãy cho chúng tôi biết trình độ tiếng anh của ban."},
                            status=status.HTTP_400_BAD_REQUEST)

        text_result, words_result, words_result_none_translate = save_or_get_new_feed_from_cache(
            word_request, level_value, object_user_request, url_source_feed, position_content)

        Thread(target=save_or_update_history_new_feed_by_url_new_feed,
               args=(object_user_request, url_source_feed, word_request,
                     text_result, position_content)).start()
    else:
        text_result, words_result, words_result_none_translate = save_or_get_new_feed_from_cache(
            word_request, None, None, url_source_feed, position_content)


    return JsonResponse({
        "text": text_result,
        "listWords": words_result,
        "list_word_not_translate": words_result_none_translate
    },
        status=status.HTTP_200_OK)


@api_view(['POST'])
def translate_new_feed_refresh(request):
    if request.user.is_authenticated:
        id_user_request = request.user.id
        object_user_request = User.objects.get(id=id_user_request)

        body_unicode = request.body.decode(BODY_DECODE)
        body = json.loads(body_unicode)
        try:
            url_source_feed = body[BODY_URL_SOURCE_FEED_REQUEST]
            position_content = body[BODY_POSITION_CONTENT_FEED_REQUEST]
            vocabularies_request = body[BODY_LIST_VOCABULARY_REQUEST]
            words_request = body[BODY_WORD_REQUEST]

            for vocabulary in vocabularies_request:
                object_vocabulary = Vocabulary.objects.get(id=vocabulary['id'])
                object_user_request = User.objects.get(id=request.user.id)
                inserted, _ = add_vocabulary_to_dairy_by_id(object_user_request, object_vocabulary,
                                                            vocabulary['type'])
        except KeyError:
            return Response({'detail': "Thiếu các tham số bắt buộc."},
                            status=status.HTTP_400_BAD_REQUEST)

        level_value = get_level_value_by_user_id(id_user_request)
        if level_value == -1:
            return Response({'detail': "Trước tiên hãy cho chúng tôi biết trình độ tiếng anh của ban."},
                            status=status.HTTP_400_BAD_REQUEST)
        text_result, words_result, words_result_none_translate = refresh_word_feed_from_cache(level_value,
                                                                                              object_user_request,
                                                                                              words_request,
                                                                                              url_source_feed,
                                                                                              position_content)

        return JsonResponse({
            "text": text_result,
            "listWords": words_result,
            "list_word_not_translate": words_result_none_translate
        },
            status=status.HTTP_200_OK)
    else:
        return form_json_require_login()


class UserVocabularyDetail(APIView):
    def delete(self, request, id_favorite_request):
        if request.user.is_authenticated:
            id_user_request = request.user.id
            count, _ = delete_favorite_by_id_favorite(id_user_request, id_favorite_request)
            if count == 0:
                return Response({'detail': "Từ yêu thích không còn tồn tại."}, status=status.HTTP_400_BAD_REQUEST)
            return Response({'detail': count}, status=status.HTTP_200_OK)
        return form_json_require_login()


class UserHistoryNewFeed(APIView):
    def get(self, request):
        if request.user.is_authenticated:
            page_request = request.GET.get('page', 1)
            object_user_request = User.objects.get(id=request.user.id)
            return Response(get_history_new_feed_by_object_user(object_user_request, page_request),
                            status=status.HTTP_200_OK)
        return form_json_require_login()
