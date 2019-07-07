FROM postgres:11.4-alpine

ENV POSTGRES_USER=omediatest
ENV POSTGRES_PASSWORD=1234

COPY OpenMedia.db.sql /docker-entrypoint-initdb.d/

HEALTHCHECK --interval=5s --timeout=5s --start-period=5s --retries=100 CMD psql -h localhost -p 5432 -U $POSTGRES_USER -v ON_ERROR_STOP=1 -c 'select version()' &> /dev/null