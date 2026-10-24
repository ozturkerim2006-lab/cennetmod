# Cennet Karanlığı (Forge 1.21.1)
1. JDK 21 kur.
2. IntelliJ IDEA ile klasörü aç (build.gradle'ı Gradle projesi olarak içe aktar).
3. Gradle senkronu bitince `genIntellijRuns` çalıştır, sonra `runClient`.
4. Jar için: `gradlew build` -> build/libs/
   (gradlew yoksa Gradle 8.8 kuruluyken proje klasöründe bir kez `gradle wrapper` çalıştır.)
Oyunda test: `/give @s cennetmod:yarik_asasi`, `/time set 18000`, asayı basılı tut.
