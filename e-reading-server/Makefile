build:
	# @pipenv lock -r > requirements.txt
	
	@echo "Build image từ docker file."
	@docker-compose build app

up: build
	@echo "Run app from docker image."
	@docker-compose up -d app

stop:
	@echo "Dừng các container đang chạy."
	@docker-compose stop

save:
	@echo "Lưu docker image nén thành file tar."
	@docker save -o ./e-reading-server-app.tar e-reading-server_app

load:
	@echo "Load docker image từ file tar."
	@docker load -i ./e-reading-server-app.tar

run:
	python manage.py runserver

installonvps:

	# Cap quyen cho file scrip
	@chmod a+x docker-entrypoint.sh
	@chmod 777 docker-entrypoint.sh

	#Copy scrip vao scrip toan he thong
	@sudo cp docker-entrypoint.sh /etc/init.d/

	# Set tu khoi dong cung boot server
	@sudo update-rc.d docker-entrypoint.sh defaults

unstallonvps:
	#Remove khỏi init
	@sudo rm /etc/init.d/docker-entrypoint.sh

	# Xoá khỏi danh sách scrip chạy với boot
	@sudo update-rc.d docker-entrypoint.sh remove

pi-deploy-app-and-tunnel:
    # Cap quyen cho file scrip
	@sudo chmod a+x /usr/src/E-Reading-server/pi/service-app.sh
	@sudo chmod 777 /usr/src/E-Reading-server/pi/service-app.sh
	@sudo chmod a+x /usr/src/E-Reading-server/pi/service-tunnel.sh
	@sudo chmod 777 /usr/src/E-Reading-server/pi/service-tunnel.sh

	# Copy scrip vao scrip toan he thong
	@sudo cp /usr/src/E-Reading-server/pi/service-app.sh /etc/init.d/
	@sudo cp /usr/src/E-Reading-server/pi/service-tunnel.sh /etc/init.d/

	# Set tu khoi dong cung boot server
	@sudo update-rc.d service-app.sh defaults
	@sudo update-rc.d service-tunnel.sh defaults

	# Run server
	@sudo service service-app.sh restart
	@sudo service service-tunnel.sh restart

pi-remove-app-and-tunnel:
    # Remove khỏi init
	#@sudo service service-app.sh stop
	#@sudo service service-tunnel.sh stop
	@sudo rm /etc/init.d/service-app.sh
	@sudo rm /etc/init.d/service-tunnel.sh

	# Xoá khỏi danh sách scrip chạy với boot
	@sudo update-rc.d service-app.sh remove
	@sudo update-rc.d service-tunnel.sh remove

pi-setup:
    # Khơi tạo môi trường
	virtualenv -p python3.6 .venv
	
	# Truy cập vào môi trường
	source .venv/bin/activate

	# Cài các gói package
	pip install -r requirements.txt

	# Khỏi tạo các file static cho trang admin
	python manage.py collectstatic

	# Cập nhật thay đổi DB
	python manage.py makemigrations

	# Thực thi các khởi tạo và thay đổi của DB
	python manage.py migrate

pi-start-service:
	@sudo service service-app.sh restart
	@sudo service service-tunnel.sh restart

azure-setup:
    # Khơi tạo môi trường
	@sudo -s virtualenv -p python3.6 /usr/src/E-Reading-server/.venv

    # Truy cập vào môi trường
	@sudo -s cd source /usr/src/E-Reading-server/

    # Cài các gói package
	@sudo -s /usr/src/E-Reading-server/.venv/bin/pip install -r requirements.txt

    # Khỏi tạo các file static cho trang admin
	@/usr/src/E-Reading-server/.venv/bin/python3.6 manage.py collectstatic

    # Cập nhật thay đổi DB
	@/usr/src/E-Reading-server/.venv/bin/python3.6 manage.py makemigrations

    # Thực thi các khởi tạo và thay đổi của DB
	@/usr/src/E-Reading-server/.venv/bin/python3.6 manage.py migrate

azure-deploy:
    # Cap quyen cho file scrip
	@sudo chmod a+x /usr/src/E-Reading-server/vps-azure/service-app.sh
	@sudo chmod 777 /usr/src/E-Reading-server/vps-azure/service-app.sh

	# Copy scrip vao scrip toan he thong
	@sudo cp /usr/src/E-Reading-server/vps-azure/service-app.sh /etc/init.d/

	# Set tu khoi dong cung boot server
	@sudo update-rc.d service-app.sh defaults

	# Run server
	@sudo service service-app.sh restart

azure-remove-service:
    # Remove khỏi init
	@sudo rm /etc/init.d/service-app.sh

	# Xoá khỏi danh sách scrip chạy với boot
	@sudo update-rc.d service-app.sh remove
