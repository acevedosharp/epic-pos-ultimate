# Epic POS Ultimate
## Configuration through environment variables
Database configuration:
- **DB_URL:** String
- **DB_USER:** String
- **DB_PASSWORD:** String

Notification configuration:
- **DO_BIRTHDAY_CHECK:** Boolean (true | false)
- **DO_PRODUCT_INVENTORY_CHECK:** Boolean (true | false)

## Setup
- Install Amazon Corretto 8 [Downloads page](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)
- Install MySQL Community 8 [Downloads page](https://dev.mysql.com/downloads/mysql/)
- Execute init.sql on the database
- Install printer driver(s)

## Build
Run `./gradlew bootJar` in project root.

## Execute
Double click on the .jar, if that doesn't work, run `execute-epic-pos-ultimate.sh` on the same folder as the generated .jar.

## Limitations
It has only been tested in Windows machines, it might work on other platforms (it should!).

**Note:** Most store printing devices only provide Windows drivers. Plus the current implementation in hardcoded to use a 48 character per line printer.
