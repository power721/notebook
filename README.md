# Notebook笔记系统

## 介绍
本站使用Vue(TypeScript) + Spring(Kotlin)开发。

UI框架： Semantic UI， Element

## 构建
mvn clean package

## 运行
java -jar target/notebook-*.jar

默认端口为8080

## 部署
 1. 上传notebook-*.jar到服务器并重命名为notebook.jar
 2. 运行config/install-service.sh自动安装为systemd服务
 3. 端口为6666，监听地址为127.0.0.1，需要使用nginx反代
 4. 修改配置文件/opt/notebook/config/application-production.yaml，设置数据库
 5. sudo systemctl restart notebook.service
