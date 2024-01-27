# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.1/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.1/reference/htmlsingle/index.html#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

DO $$
DECLARE
max_id INT;
BEGIN
SELECT MAX(id) INTO max_id FROM contact;
EXECUTE 'ALTER SEQUENCE contact_id_seq RESTART WITH ' || COALESCE(max_id + 1, 1);
END $$;


DO $$
DECLARE
max_id INT;
BEGIN
SELECT MAX(id) INTO max_id FROM contact_email;
EXECUTE 'ALTER SEQUENCE contact_email_id_seq RESTART WITH ' || COALESCE(max_id + 1, 1);
END $$;

DO $$
DECLARE
max_id INT;
BEGIN
SELECT MAX(id) INTO max_id FROM contact_phone;
EXECUTE 'ALTER SEQUENCE contact_phone_id_seq RESTART WITH ' || COALESCE(max_id + 1, 1);
END $$;

DO $$
DECLARE
max_id INT;
BEGIN
SELECT MAX(id) INTO max_id FROM contact_address;
EXECUTE 'ALTER SEQUENCE contact_address_id_seq RESTART WITH ' || COALESCE(max_id + 1, 1);
END $$;