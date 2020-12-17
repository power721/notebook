#!/usr/bin/env bash

USERNAME=notebook
APPNAME=notebook

sudo useradd -r -m ${USERNAME}
sudo mkdir -p /opt/${APPNAME}/config
sudo mkdir -p /opt/${APPNAME}/log
if [[ ! -f /opt/${APPNAME}/config/application-production.yaml ]]; then
cat <<EOT > /opt/${APPNAME}/config/application-production.yaml
cn:
  har01d:
    auth:
      jwt:
        secretKey: "ThisIsMyJWTSecretKeyAtLeast256bit!"
spring:
  datasource:
    username: ${USERNAME}
    password: password
logging:
  file:
    name: /opt/notebook/log/notebook.log
EOT
fi
sudo chown -R ${USERNAME}:${USERNAME} /opt/${APPNAME}/

cat <<EOT > ${APPNAME}.service
[Unit]
Description=${APPNAME} API
After=syslog.target

[Service]
User=${USERNAME}
WorkingDirectory=/opt/${APPNAME}
ExecStart=/opt/${APPNAME}/${APPNAME} --spring.profiles.active=production
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOT

[[ -f /etc/systemd/system/${APPNAME}.service ]] || sudo cp ${APPNAME}.service /etc/systemd/system/
sudo cp ${APPNAME}.jar /opt/${APPNAME}/${APPNAME}
sudo chown ${USERNAME}:${USERNAME} /opt/${APPNAME}/${APPNAME}
sudo chmod a+x /opt/${APPNAME}/${APPNAME}

sudo systemctl enable ${APPNAME}.service
sudo systemctl restart ${APPNAME}.service
sleep 3
sudo systemctl status ${APPNAME}.service
