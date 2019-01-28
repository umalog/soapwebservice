SOAP веб-сервис. 
В настройках рекомендуется изменить:
`plannedFileSize` - размер каждого файла;
`batchSize` - блоки записи(они же максимальная величина погрешности);
`fileNames` - перечисленные через запятую имена файлов, которые нужно создать.


* _http://localhost:8080/ws/finder.wsdl_
Подергать можно командой:
* _curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/ws_
 