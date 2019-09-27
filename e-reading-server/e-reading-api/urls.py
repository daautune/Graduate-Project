from django.urls import path

from .views import index, UserView, api_convert_work, UserRegistrationAPIView, LevelOfUser, \
    UserVocabularyFavorite, translate_new_feed, translate_new_feed_refresh, UserVocabularyDetail, UserHistoryNewFeed, \
    login

urlpatterns = [
    path('v1/', index),

    # Authentication
    path('signin', login, name='index_login'),
    path('register', UserRegistrationAPIView.as_view(), name='index_register'),
    path('signout', UserView.as_view(), name='index_logout'),

    # Account
    path('v1/portal/level', LevelOfUser.as_view(), name='LevelEnglish'),
    path('v1/portal/favorite', UserVocabularyFavorite.as_view(), name='VocabularyFavorite'),
    path('v1/portal/favorite/<int:id_favorite_request>', UserVocabularyDetail.as_view(),
         name='Vocabulary_favorite_delete'),
    path('v1/portal/history/new_feed', UserHistoryNewFeed.as_view(), name='history_new_feed'),

    # Translate
    path('v1/translate/word', api_convert_work, name='detect_word'),
    path('v1/translate/feed', translate_new_feed, name='detect_word_from_new_feed'),
    path('v1/translate/feed/refresh', translate_new_feed_refresh, name='detect_word_from_new_feed_refresh'),
]
