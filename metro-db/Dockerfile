FROM mysql:8.0

# Environment variables for database setup
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=metro
ENV MYSQL_USER=metro
ENV MYSQL_PASSWORD=metro

# Copy initialization SQL scripts
COPY ./init.sql /docker-entrypoint-initdb.d/

# Set default character set and collation
RUN echo "[mysqld]\ncharacter-set-server=utf8mb4\ncollation-server=utf8mb4_unicode_ci" > /etc/mysql/conf.d/charset.cnf

# Expose MySQL port
EXPOSE 3306

CMD ["mysqld"] 