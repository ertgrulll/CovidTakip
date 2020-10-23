# Covid Takip

*Covid Takip* uygulamas�, salg�ndan korunmay� ve T�rkiye-D�nya verilerinin g�rselle�tirilerek anla��lmas�n�n kolayla�mas�n� ama�layan, Java ile yaz�lm�� bir android projesidir. BLE(bluetooth low energy) destekli �ip sete sahip cihazlarda(android 5.0-api level 21+ %97) beacon paketleri yay�nlar ve di�er cihazlardan gelen paketleri taray�p ayn� UUID'ye sahip olanlar� ��zerek gelen bilgileri i�ler. Bu paketler hexadecimal formatta �unlar� ta��r:

- Uygulamaya �zg� bir id, 
- Kullan�c�ya �zg� bir id,
- Hastal�k veya ��phe durumu.

### �zellikler
- ###### �nternet ba�lant�s� gerektirmeden riskli durumlarda **anl�k uyar�** ve bildirim sa�lanmas�:
![gif](cagif/suspicion.gif)  ![gif](cagif/notification.gif)
- ###### Demografik da��l�m ve kullan�c� ya��na g�re risk belirlenmesi ve dikkat edilmesi gerekenler:
![gif](cagif/detay_home.gif)  ![gif](cagif/oneri.gif)
- ###### Covid testi:
![gif](cagif/test_hpme.gif)
- Covid ba�lang�c�ndan g�n�m�ze **se�ilen tarih aral���ndaki** Covid-19 istatistik verilerinin 6 kategoride grafiklerle g�sterimi:
![gif](cagif/local_graphs.gif)
- D�nya geneli �statistikler, 217 �lke aras�ndan **her yenilendi�inde** rastgele se�ilen 5 �lkenin yine rastgele se�ilen 4 farkl� kategorideki verilerinin grafikle�tirilmesi:
![gif](cagif/global_graph.gif)
- 

### Kurulum
Proje k�k dizinindeki apk dosyas�n� android bir telefona kurarak denenebilir veya proje klonlan�p Android Studio ile �al��t�r�labilir.

##### Not:
Proje, geli�tirmek veya kullanmak isteyen herhangi birine a��kt�r. Payla�ma amac�m olmadan NKU staj projesi olarak yazd�m.