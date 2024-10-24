### Sau khi có file Dockerfile ta tiến hành build image có tên "identity-service" với version 0.0.1
    `docker build -t identity-service:0.0.1 .`

### Chạy image có tên vừa tạo là " identity-service:0.0.1" và đặt tên contairner là "identity-service" port "8080", biến môi trường "DBMS_CONNECTION=jdbc:mysql://172.17.0.2:3306/identity_service identity-service:0.0.1"
    `docker run --name identity-service -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://172.17.0.2:3306/identity_service identity-service:0.0.1 identity-service:0.0.1`
### tạo network với tên sondev-network
    `docker network create sondev-network`

### Kiểm tra các network đang có
    `docker network ls`

### Khởi chạy container có tên "mysql-identity-service" với image đã có "mysql:8.0.39-debian" với network vừa tạo là "sondev-network" mật khẩu = root port 3306
    `docker run --network sondev-network --name mysql-identity-service -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.39-debian`

###  Chạu ứng dụng "identity_service" network "sondev-network" port "8080" với cấu hình biến môi trường "DBMS_CONNECTION=jdbc:mysql://mysql-identity-service:3306/identity_service" từ image có tên "identity-service:0.0.1"
    `docker run --name identity-service --network sondev-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql-identity-service:3306/identity_service identity-service:0.0.1`

## Chuẩn bị publish image lên docker hub
### build image
    `docker build -t hoangson93/identity-service:0.9.0 .`

### push image lên docker hub
    `docker image push hoangson93/identity-service:0.9.0`

## pull image về local
    `docker pull hoangson93/identity-service:0.9.0`

## chạy image vừa pull về
    `docker run --name identity-service --network sondev-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql-identity-service:3306/identity_service hoangson93/identity-service:0.9.0`