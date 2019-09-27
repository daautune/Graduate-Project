from django.contrib import admin
from .models import Vocabulary, VocabularyFavorite, RequestHistory, VocabularyType

# Register your models here.
# This is only test
admin.site.register(Vocabulary)
admin.site.register(RequestHistory)
admin.site.register(VocabularyFavorite)
admin.site.register(VocabularyType)
