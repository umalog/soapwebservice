SOAP веб-сервис.

Cборщик - maven, БД - PostgreSQL

 Properties описание:
`plannedFileSize` - размер каждого файла;
`batchSize` - блоки записи(они же максимальная величина погрешности);
`fileNames` - перечисленные через запятую имена файлов, которые нужно создать.


* _http://localhost:8080/ws/finder.wsdl_
Пример запроса лежит в корне проекта:
* _curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/ws_