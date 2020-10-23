# Covid Takip

*Covid Takip* uygulamasý, salgýndan korunmayý ve Türkiye-Dünya verilerinin görselleþtirilerek anlaþýlmasýnýn kolaylaþmasýný amaçlayan, Java ile yazýlmýþ bir android projesidir. BLE(bluetooth low energy) destekli çip sete sahip cihazlarda(android 5.0-api level 21+ %97) beacon paketleri yayýnlar ve diðer cihazlardan gelen paketleri tarayýp ayný UUID'ye sahip olanlarý çözerek gelen bilgileri iþler. Bu paketler hexadecimal formatta þunlarý taþýr:

- Uygulamaya özgü bir id, 
- Kullanýcýya özgü bir id,
- Hastalýk veya þüphe durumu.

### Özellikler
- ###### Ýnternet baðlantýsý gerektirmeden riskli durumlarda **anlýk uyarý** ve bildirim saðlanmasý:
![gif](cagif/suspicion.gif)  ![gif](cagif/notification.gif)
- ###### Demografik daðýlým ve kullanýcý yaþýna göre risk belirlenmesi ve dikkat edilmesi gerekenler:
![gif](cagif/detay_home.gif)  ![gif](cagif/oneri.gif)
- ###### Covid testi:
![gif](cagif/test_hpme.gif)
- Covid baþlangýcýndan günümüze **seçilen tarih aralýðýndaki** Covid-19 istatistik verilerinin 6 kategoride grafiklerle gösterimi:
![gif](cagif/local_graphs.gif)
- Dünya geneli Ýstatistikler, 217 ülke arasýndan **her yenilendiðinde** rastgele seçilen 5 ülkenin yine rastgele seçilen 4 farklý kategorideki verilerinin grafikleþtirilmesi:
![gif](cagif/global_graph.gif)
- 

### Kurulum
Proje kök dizinindeki apk dosyasýný android bir telefona kurarak denenebilir veya proje klonlanýp Android Studio ile çalýþtýrýlabilir.

##### Not:
Proje, geliþtirmek veya kullanmak isteyen herhangi birine açýktýr. Paylaþma amacým olmadan NKU staj projesi olarak yazdým.