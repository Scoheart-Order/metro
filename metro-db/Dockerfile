FROM mysql:8.0

# Environment variables for database setup
ENV MYSQL_ROOT_PASSWORD=88888888
ENV MYSQL_DATABASE=metro
ENV MYSQL_USER=metro
ENV MYSQL_PASSWORD=metro

# Copy initialization SQL scripts
COPY ./init.sql /docker-entrypoint-initdb.d/
COPY ./data.sql /docker-entrypoint-initdb.d/

# Set default character set and collation
CMD ["--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]

# Configure healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=5 --start-period=30s \
  CMD mysqladmin ping -h localhost -u metro -pmetro || exit 1

# Expose MySQL port
EXPOSE 3306 