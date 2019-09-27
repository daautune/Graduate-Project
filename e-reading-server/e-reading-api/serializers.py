import maya
from django.conf import settings
from django.contrib.auth import get_user_model
from django.utils import timezone
from rest_framework import serializers

from .models import VocabularyFavorite, Vocabulary, VocabularyType, RequestHistory

User = get_user_model()


class UserRegistrationSerializer(serializers.ModelSerializer):
    username = serializers.CharField(required=True, error_messages={"blank": "Usename không được để trống."})

    email = serializers.EmailField(required=True, error_messages={"blank": "Email không được để trống."})

    password = serializers.CharField(required=True, error_messages={"blank": "Password không được để trống."})

    password_confirm = serializers.CharField(required=True,
                                             error_messages={"blank": "Nhập lại mật khẩu không được để trống."})

    class Meta:
        model = User
        fields = ['username', 'email', 'password', 'password_confirm']

    def validate_email(self, value):
        """
        Check email đã tồn tại trong db hay chưa?
        """
        if User.objects.filter(email=value).exists():
            raise serializers.ValidationError("Email đã được sử dụng.")
        return value

    def validate_password(self, value):
        """
        Kiểm tra độ dài của mật khẩu  
        """
        if len(value) < getattr(settings, 'PASSWORD_MIN_LENGTH', 8):
            raise serializers.ValidationError(
                "Mật khẩu an toàn phải dài hơn %s kí tự." % getattr(
                    settings, 'PASSWORD_MIN_LENGTH', 8))
        return value

    def validate_password_confirm(self, value):
        """
        Nhập lại passwork
        """
        data = self.get_initial()
        password = data.get('password')
        if password != value:
            raise serializers.ValidationError("Nhập lại mật khẩu sai.")
        return value

    def validate_username(self, value):
        """
        Kiểm tra tài khoản đăng nhập có tồn tại trong db chưa 
        """
        if User.objects.filter(username=value).exists():
            raise serializers.ValidationError("Tài khoản đã có người sử dụng")
        return value

    def create(self, validated_data):
        user_data = User(
            username=validated_data.get('username'),
            email=validated_data.get('email'),
            password=validated_data.get('password'),
        )
        user_data.set_password(self.initial_data['password'])
        user_data.save()
        return validated_data


class ListVocabularyFavoriteSerializer(serializers.ModelSerializer):
    time_create = serializers.SerializerMethodField(read_only=True)  # Chi cho phep GET
    vocabulary_word = serializers.SerializerMethodField(read_only=True)
    vocabulary_mean_short = serializers.SerializerMethodField(read_only=True)

    def get_time_create(self, vocabulary):
        date_time = maya.parse(vocabulary.time_add).datetime(to_timezone=timezone.get_default_timezone_name(),
                                                             naive=False)
        return date_time.strftime("%d-%m-%y %H:%M:%S")

    def get_vocabulary_word(self, vocabulary):
        voca = Vocabulary.objects.filter(id=vocabulary.vocabulary_id.id).first()
        return voca.word

    def get_vocabulary_mean_short(self, vocabulary):
        mean = VocabularyType.objects.filter(id_vocabulary__id=vocabulary.vocabulary_id.id,
                                             type_vocabulary=vocabulary.type_vocabulary).only('mean_short').first()
        if mean is not None:
            return mean.mean_short
        else:
            mean = VocabularyType.objects.filter(id_vocabulary__id=vocabulary.vocabulary_id.id,
                                                 type_vocabulary='NOUN').only('mean_short').first()
            if mean is not None:
                return mean.mean_short

    class Meta:
        model = VocabularyFavorite
        fields = (
            'id', 'time_create', 'is_hard', 'vocabulary_id', 'vocabulary_word', 'vocabulary_mean_short',
            'type_vocabulary')


class VocabularyFavoriteSerializer(serializers.ModelSerializer):
    class Meta:
        model = VocabularyFavorite
        fields = '__all__'


class ListHistoryNewFeedSerializer(serializers.ModelSerializer):
    time_create = serializers.SerializerMethodField(read_only=True)

    def get_time_create(self, request_history):
        date_time = maya.parse(request_history.time_request).datetime(to_timezone=timezone.get_default_timezone_name(),
                                                                      naive=False)
        return date_time.strftime("%d-%m-%y %H:%M:%S")

    class Meta:
        model = RequestHistory
        fields = '__all__'
