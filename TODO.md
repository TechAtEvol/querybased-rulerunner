# Arbetslista för utvecklare
- Skapa en minimal REST-tjänst i Quarkus och testa att det finns en endpoint GET /organisationer/:id som svarar med en godtycklig strängLägg och testa att det finns en POST /organisationer som svarar med en godtycklig sträng
- Ändra och testa så att POST /organisationer kan ta ett KYC-objekt och sparar det i en dokumentdatabas, tabellen ska heta organisationer
- Ändra och testa så att GET /organisationer/:id läser från databas och ger tillbaka när ett KYC-objekt har matchande id
- Lägg till och testa en POST /regler som tar ett objekt som just nu bara innehåller en key, key ska heta query och vara av typen sträng samt tabellen ska heta regler
- Lägg till och testa en GET /regler som ger tillbaka en lista av alla regler
- Lägg till och testa en PUT /regler/:regel-id som gör att man kan ändra på regler
- Ändra på strängen för query, så att den kontroller om följande nyckel finns på organisation "organisation.juridiskForm.kod=96"
- Lägg till och testa en GET /kontroller/regler/:regel-id som kör regelns query och ger tillbaka id på de KYC-objekt som motsvarade
- Lägg till ett testdataobjekt till organsationer, som är framtaget för skattekontroller
- Lägg till och testa en till regel med en query som tittar om en av organisation.skatt[registreringar] har ' "typ.kod"="ARBETSGIVARE" AND "aktiv"="false" '
- Lägg till och testa så att våra regler har nycklarna namn och källa och hämta namn från test-data sidan och  vilket lagstöd som finns för regeln