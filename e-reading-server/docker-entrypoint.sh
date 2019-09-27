#! /bin/bash                                                                                                 
                                                                                                             
cd /home/E-Reading-server
                                                                                                             
source .venv/bin/activate                                                                                    
                                                                                                             
pip install -r requirements.txt
                                                                                                             
python manage.py collectstatic
                                                                                                             
python manage.py makemigrations
                                                                                                             
python manage.py migrate
                                                                                                             
python manage.py runserver 172.245.123.121:8000
                                                                                                             
exec "$@"
