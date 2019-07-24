FROM mariadb:10.1.40-bionic

ENV MYSQL_DATABASE=omedia
ENV MYSQL_USER=omediauser
ENV MYSQL_PASSWORD=omediauser$
ENV MYSQL_RANDOM_ROOT_PASSWORD=yes

COPY Omedia_mysql.sql /docker-entrypoint-initdb.d/

HEALTHCHECK CMD mysql --user=$MYSQL_USER --password=$MYSQL_PASSWORD -e "show databases;"