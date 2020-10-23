# Covid Takip

*Covid Takip* uygulaması, salgından korunmayı ve Türkiye-Dünya verilerinin görselleştirilerek anlaşılmasının kolaylaşmasını amaçlayan, Java ile yazılmış bir android projesidir. BLE(bluetooth low energy) destekli çip sete sahip cihazlarda(android 5.0-api level 21+ %97) beacon paketleri yayınlar ve diğer cihazlardan gelen paketleri tarayıp aynı UUID'ye sahip olanları çözerek gelen bilgileri işler. Bu paketler hexadecimal formatta şunları taşır:

- Uygulamaya özgü bir id, 
- Kullanıcıya özgü bir id,
- Hastalık veya şüphe durumu.

### Özellikler
<ul>
  <li><b>İnternet bağlantısı gerektirmeden riskli durumlarda anlık uyarı ve bildirim sağlanması:</b><br>
    <p float="left">
      <img src="cagif/suspicion.gif" width="216" height="460">
      <img src="cagif/notification.gif" width="216" height="460" hspace="40">
    </p>
  </li>
  <li><b>Demografik dağılım ve kullanıcı yaşına göre risk belirlenmesi ve dikkat edilmesi gerekenler:</b><br>
    <p float="left">
      <img src="cagif/detay_home.gif" width="216" height="460">
      <img src="cagif/oneri.gif" width="216" height="460" hspace="40">
    </p>
  </li>
  <li><b>Covid testi: </b><br>
    <img src="cagif/test_home.gif" width="216" height="460">
  </li>
  <li><b>Covid başlangıcından günümüze seçilen tarih aralığındaki Covid-19 istatistik verilerinin 6 kategoride grafiklerle gösterimi:</b><br>
    <img src="cagif/local_graphs.gif" width="216" height="460">
  </li>
  <li><b>Dünya geneli İstatistikler, 217 ülke arasından her yenilendiğinde rastgele seçilen 5 ülkenin yine rastgele seçilen 4 farklı kategorideki verilerinin grafikleştirilmesi: </b><br>
    <img src="cagif/global_graph.gif" width="216" height="460">
  </li>
</ul>

### Kurulum
Proje kök dizinindeki apk dosyasını android bir telefona kurarak denenebilir veya proje klonlanıp Android Studio ile çalıştırılabilir.

##### Not:
Proje, geliştirmek veya kullanmak isteyen herhangi birine açıktır. Paylaşma amacım olmadan NKU staj projesi olarak yazdım.
