# SocketProject

# AFTP bestandsprotocol 
Het protocol is state-less. Het maakt dus niet uit welke requests de client eerder in de sessie gestuurd heeft. New lines bestaan uit \r\n . Alle tekst behalve de content wordt UTF-8 gecodeerd.  

## Status codes 
De status code bestaat altijd uit een drie cijferige code gevolgd door de tekstuele beschrijving van die code.  
De mogelijke statuscodes zijn:  
200 OK – De request is met succes uitgevoerd. De response bevat het gevraagde antwoord  
400 Bad request – De request voldoet niet aan het protocol. De server snapt niet wat de bedoeling is.  
404 Not found – De request kan niet uitgevoerd worden. De opgevraagde data kan niet worden gevonden.  
418 Gone – Het bestand in de opgegeven versie bestaat niet.  
423 Locked – Het bestand wordt als door een andere client beschreven.  
500 Server Error – De request was goed, maar er gaat iets niet goed in de server waardoor de request niet beantwoord kan worden.

## Headers 
Headers zijn extra gegevens over requests en responses. De keys zijn hoofdletterongevoelig. De waarde is afhankelijk van de key wel hoofdlettergevoelig. Een header bestaat altijd uit een key gevolgd door een dubbele punt en een spatie gevolgd door de waarde van de header.

## UNIX-timestamp 
De UNIX-timestamp is een standaardformaat in UNIX om datumtijd aan te geven. De timestamp is een getal bestaande uit het aantal seconden sinds 1 januari 1970 om middernacht.

## Requests 
De eerste regel van een request bestaat uit het commando, een spatie en een relatief pad. Dit pad begint altijd met een slash (/). De regel vervolgt daarna met een spatie, het woord AFTP gevolgd door een slash (/) gevolgd door de versie. Op dit moment alleen versie 1.0.  
Op volgende regels volgen de headers, één op elke regel. De headers eindigen wanneer een lege regel wordt gestuurd.  
Eventuele content volgt dan na die lege regel. De hoeveelheid content is gelijk aan de waarde van de header Content-Length bytes.

## Responses 
Een response bestaat uit het woord AFTP gevolgd door een slash (/) gevolgd door de versie. Op dit moment alleen versie 1.0. De regel wordt vervolgd met de status code. Op volgende regels volgen de response headers gevolgd door een lege regel. Eventueel volgt daarna nog content de hoeveelheid content is gedefinieerd in de Content-Length header. De header geeft aan hoeveel bytes de content lang is.

## Bestandlijst opvragen 
De volledige bestandslijst kan worden opgevraagd met het commando LIST. Dit gebeurt wanneer een nieuwe applicatie wordt aangesloten. Het pad kan aangevuld worden om de response te beperken tot het opgegeven pad. Meestal zal de applicatie alleen vragen om de wijzigingen sinds zijn laatste request. Dit gebeurt door de header Since mee te geven gevolgd door de UNIX-timestamp vanaf wanneer wijzigingen teruggegeven moeten worden.  
De content van de responses op deze requests zijn in beide gevallen een lijst met bestandsnamen gevold door een spatie en een UNIX timestamp wanneer dit bestand voor het laatst is aangepast gevolgd door een spatie en de md5 hash van de inhoud van het bestand. Eén bestand per regel. 

## Bestand opvragen 
Een bestand kan worden opgevraagd met het commando GET en het pad van het bestand.  
De content van de response zal zijn de inhoud van het bestand. Als het bestand niet gevonden wordt, wordt een response met statuscode 404 teruggegeven. De response zal dan geen content bevatten.

## Bestand aanmaken/aanpassen 
Een bestand kan worden aangemaakt of aangepast door het commando PUT. In het pad staat de bestandsnaam. De header Last-Modified geeft aan wanneer het bestand voor het laatst is aangepast als UNIX-timestamp. De content van het request wordt gevuld met de content van het bestand. Wanneer het bestand aangepast moet worden moet de ETag header worden meegegeven. De waarde van deze header bevat de op de client bekende vorige md5 hash van de content van het bestand.  
De content van de response wordt bij succes gevuld met de md5 hash van de content van het ontvangen bestand. De response code geeft aan of het gelukt is.

## Bestand verwijderen 
Een bestand kan worden verwijderd door het commando DELETE. In het pad staat de bestandsnaam. De ETag header moet worden meegegeven. De waarde van deze header bevat de op de client bekende vorige md5 hash van de content van het bestand.  
De response code geeft aan of het gelukt is. 

## Voorbeeld
\>LIST / AFTP/1.0  
\>  
\<AFTP/1.0 200 OK  
\<Content-Length: xxx  
\<  
\<Tekst.txt 1575800743 25d7f36115f74a2fa7c6d185ab008588  
\<Afbeelding.jpg 1575800657 99f4f1eba9e7ce89a7d5b2e8631dcfc5  
\>GET /Tekst.txt AFTP/1.0  
\>  
\<AFTP/1.0 200 OK  
\<Content-Length: xxx  
\<  
\<RGl0IGlzIGVlbiB0ZWtzdCBiZXN0YW5kCk1ldAoKZWVuIGFhbnRhbCAKCm5ldwpsaW5lcwoK  
\>PUT /Tekst.txt AFTP/1.0  
\>Content-Length: xxx  
\>ETag: 25d7f36115f74a2fa7c6d185ab008588  
\>  
\>RGl0IGlzIGVlbiBhbmRlciB0ZWtzdCBiZXN0YW5kCk1ldAoKZWVuIGFhbnRhbCAKCm5ldwpsaW5lcwoK  
\<AFTP/1.0 200 OK  
\<Content-Length: xxx  
\<  
\<b02c4735bc5bd845f94b0e8b515c3303  
\>LIST / AFTP/1.0  
\>Since: 1575800744  
\>  
\<AFTP/1.0 200 OK  
\<Content-Length: xxx  
\<  
\<Tekst.txt 1575800744 b02c4735bc5bd845f94b0e8b515c3303
