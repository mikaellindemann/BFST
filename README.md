Førsteårsprojekt: Danmarkskort: Visualisering, Navigation, Søgning og Ruteplanlægning
====
#Kørsel af programmet.
Kompiler projektet med NetBeans version 7.4 eller nyere.
Kør Map.jar.

Du vil blive spurgt om hvilken data du vil bruge. Vælg Krak for hurtigere indlæsning, og vælg OpenStreetMap for mere detaljerede og nyere data.

Hvis programmet ikke bliver færdig med at indlæse i løbet af højst et par minutter skal jar-filen køres med argumentet -Xmx2000m for at give programmet mere hukommelse at arbejde med. Fra kommandolinien skal der altså skrives "java -Xmx2000m -jar ./Map.jar".

Hvis 'lib' og 'res' mappen bliver flyttet i forhold til programfilen 'Map.jar' virker programmet ikke.

#Nye data fra OpenStreetMap.
Programmet kan bruge data fra OpenStreetMap. I den færdige udgave af programmet har vi ikke tiltænkt at man selv skal kunne skabe disse data, da det er et langsomt program og der sikkert kan opstå en masse fejl.
Dog kan man på nuværende tidspunkt køre en af Parserne fra NetBeans-projektet ved at køre den enkelte fil.

#Tip til brug
Hvis du er færdig med at bruge en rutevejledning kan du med fordel nulstille denne, da programmet så kan arbejde hurtigere.