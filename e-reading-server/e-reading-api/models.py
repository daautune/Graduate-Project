from django.contrib.auth.models import User
from django.db import models


# Create your models here.
class RequestHistory(models.Model):
    user_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='fk_document_user',
        db_column='user_id')
    word = models.TextField()
    word_result = models.TextField()
    time_request = models.DateTimeField(auto_now_add=True)
    url_new_feed = models.CharField(max_length=125, null=True)
    url_thumbnail_new_feed = models.CharField(max_length=125, null=True)
    title_new_feed = models.TextField(null=True)
    introduction_new_feed = models.TextField(null=True)

    objects = models.Manager()
    DoesNotExist = models.ObjectDoesNotExist

    class Meta:
        db_table = 'tbl_request_history'
        unique_together = ('user_id', 'url_new_feed')


class Vocabulary(models.Model):
    word = models.TextField(db_index=True, max_length=50)
    pronucation = models.CharField(max_length=50)
    popularity = models.BigIntegerField(default=0)

    objects = models.Manager()
    DoesNotExist = models.ObjectDoesNotExist

    class Meta:
        db_table = 'tbl_vocabulary'
        unique_together = ('word', 'pronucation')


class VocabularyFavorite(models.Model):
    user_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='fk_user_id_favorite',
        db_column='user_id')
    vocabulary_id = models.ForeignKey(
        Vocabulary,
        on_delete=models.CASCADE,
        related_name='fk_vocabulary_id_favorite',
        db_column='vocabulary_id')
    type_vocabulary = models.CharField(max_length=8)
    time_add = models.DateTimeField(auto_now_add=True)
    is_hard = models.BooleanField(default=False)

    objects = models.Manager()

    class Meta:
        db_table = 'tbl_vocabulary_favorite'
        unique_together = ('user_id', 'vocabulary_id', 'type_vocabulary')

    def save(self, force_insert=False, force_update=False, using=None,
             update_fields=None):
        """
        Check tồn tại trước khi lưu bào DB
        :param force_insert:
        :param force_update:
        :param using:
        :param update_fields:
        :return:
        """
        if not VocabularyFavorite.objects.filter(user_id=self.user_id, vocabulary_id=self.vocabulary_id,
                                                 type_vocabulary=self.type_vocabulary).exists():
            return 1, super().save()
        else:
            return 0, super().unique_error_message(VocabularyFavorite, ('user_id', 'vocabulary_id', 'type_vocabulary'))


class VocabularyType(models.Model):
    id_vocabulary = models.ForeignKey(
        Vocabulary,
        on_delete=models.CASCADE,
        related_name='fk_vocabulary_id_type',
        db_column='id_vocabulary')
    type_vocabulary = models.CharField(db_index=True, max_length=8)
    mean_short = models.TextField(max_length=125)
    mean = models.TextField()
    example = models.TextField()

    objects = models.Manager()

    class Meta:
        db_table = 'tbl_vocabulary_type'
        unique_together = ('id_vocabulary', 'type_vocabulary')


class UserExtend(models.Model):
    """
    Mở rộng bảng user-authen
    """
    user_id = models.OneToOneField(
        User, on_delete=models.CASCADE, db_column='user_id')
    level_user = models.IntegerField(default=0, null=True)

    objects = models.Manager()
    DoesNotExist = models.ObjectDoesNotExist

    class Meta:
        db_table = 'tbl_user_info'
        unique_together = ('user_id',)
