package services.and.receivers;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
    public final static String FB_NETWORK_EXC_MSG = "İnternet bağlantınız ile ilgili bir sorun yaşanıyor, lütfen doğru çalışan bir bağlantıya sahip" +
            " olduğunuzdan emin olup tekrar deneyin";

    public final static String FB_AUTH_USER_COLLISION_EXC_MSG = "E-posta adresi başka bir hesapta kullanılıyor, şifrenizi unuttuysanız 'Giriş Yap' " +
            "butonuna tıklayarak değiştirebilirsiniz";

    public final static String FB_WEAK_PASS_EXC_MSG = "En az 6 karakter içeren bir şifre seçin";

    public final static String FB_API_NOT_AVAILABLE_EXC_MSG = "Sıradışı bir hata yaşanıyor, lütfen bu hatayı bize bildirin";

    public final static String FB_AUTH_INVALID_CREDENTIALS_PASS_EXC_MSG = "Hatalı bir şifre girdiniz, şifrenizi sıfırlamak için 'Şifremi unuttum' " +
            "butonuna dokunabilirsiniz";

    public final static String FB_AUTH_INVALID_CODE = "Hatalı bir doğrulama kodu girdiniz. 'KAYIT OL' butonuna dokunarak tekrar sms " +
            "gönderebilirsiniz.";

    public static final String FB_AUTH_INVALID_PHONE_TYPE = "Hatalı bir telefon numarası girdiniz, numarayı kontrol edip tekrar deneyin.";

    public final static String FB_AUTH_INVALID_CREDENTIALS_FORMAT_EXC_MSG = "Hatalı yapıda bir e-posta adresi girdiniz, lütfen adresi kontrol edin" +
            "(Ör:adres@epostasağlayıcı.com)";

    public final static String FB_EXC_MSG = "Beklenmedik bir hata oluştu, lütfen bunu bize bildirin";

    public final static String FB_AUTH_EMAIL_EXC_MSG = "Şifre sıfırlama e-postası gönderme ile ilgili bir sorun yaşıyoruz, lütfen daha sonra tekrar" +
            " deneyin";

    public final static String FB_AUTH_INVALID_USER_EXC_MSG = "Bu e-posta adresi ile bağlantılı bir hesap bulamadık, yeni bir " +
            "hesap oluşturmak için önceki sayfaya gidin";

    public final static String FB_TOO_MANY_REQUESTS_EXC_MSG = "Çok sayıda yanlış şifre girildiği için hesabınız bloke edildi, şifrenizi sıfırlayın " +
            "veya daha sonra deneyin";

    public final static String UNKNOWN_ERROR_MSG = "Bir şeyler ters gitti, lütfen daha sonra tekrar deneyin";

    public final static String PHONE_VERIFY_ERR = "Telefon numaranızı doğrulama ile ilgili bir sorun yaşıyoruz, lütfen daha " +
            "sonra tekrar deneyin.";

    public static final String FIELD_CHECKER_NAME_ERR = "Lütfen gerçek adınızı ve soyadınızı girin";

    public static final String FIELD_CHECKER_MAIL_ERR = "Lütfen geçerli bir e-posta adresi girin";

    public static final String FIELD_CHECKER_PHONE_ERR = "Telefon numaranızı 10 haneli olacak şekilde girin";

    public static final String FIELD_CHECKER_PASS_ERR = "Lütfen en az 6 haneli bir şifre girin, şifreniz boşluk içeremez";

    public static final String FIELD_CHECKER_DEFAULT_ERR = "Bir şeyler ters gitti, lütfen daha sonra tekrar deneyin";

    public final static String PASS_RESET_MSG = "Şifrenizi sıfırlamak için size bir e-posta gönderdik, lütfen gelen kutunuzu kontrol edin";

    public final static String PASS_RESET_INTERVAL_MSG = "60 saniyede bir şifre sıfırlama e-postası gönderebilirsiniz";

    public static final String EMPTY_MAIL_FIELD_MSG = "E-posta adresi alanını doldurun";

    public static final String CONNECTION_ERR_MSG = "Çalışan bir internet bağlantınız yok\n veya bağlantınız çok yavaş.";

    public static final String CONNECTION_ERR_MSG_LAST = "Lütfen çalışan bir internet bağlantısına sahip olduğunuza emin olup tekrar deneyin";

    public static final String BT_RES_CANCELED_MSG = "Temas takibi için bluetooth özelliğinin açık olması gerekiyor, lütfen bluetooth'u aktif hale" +
            " getirin";

    public static final String LOCATION_PERMISSION_TITLE = "Uygulamanın doğru çalışabilmesi için konum izni gerekli";

    public static final String LOCATION_PERMISSION_MSG = "CovidTakip, konumunuzu takip veya kayıt etmiyor. Bazı Bluetooth özelliklerinin " +
            "kullanılabilmesi için bu izin gerekli, detaylı bilgi için e-posta adresinize gönderilecek olan 'İzinler & Gizlilik Sözleşmesi'ne" +
            " göz atabilirsiniz.";

    public static final String PERMISSIONS_LIMITED_FUNC = "CovidTakip sınırlandırılmış özelliklerle çalışıyor...";

    public static final String LOCATION_PERMISSION_REJECT_MSG = "Konum izni verilmediği için CovidTakip yakındaki diğer BT " +
            "cihazları tarayamıyor. Uygulamanın doğru çalışabilmesi için lütfen Ayarlar -> Uygulamalar -> İzinler'e giderek CovidTakip'e izin verin.";

    public static final String  NOTIFICATION_CHANNEL_ID = "501";

    public static final String NOTIFICATION_NAME = "Mesafe Taraması";

    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Olası temas taraması";

    public static final String FOREGROUND_NOTIFICATION_TITLE = "Olası riskleri tarıyor";

    public static final String NOTIFICATION_RISK_TITLE = "Risk tespit edildi!";

    public static final String NOTIFICATION_RISK_CONTENT = "Bulunduğunuz yerden uzaklaşın";

    public static final String APP_UUID_STR = "cvdtkp";

    public static String PREF_FILE_NAME = "prefs";

    public static final String NORMAL = "0";

    public static final String SUSPICIOUS = "1";

    public static final String CASE = "2";

    public static final String SUSPICIOUS_MSG_HOME = "Belirti gösterdiğini düşünen bir kullanıcı ile temasınız oldu.";

    public static final String CASE_MSG_HOME = "Enfekte bir kullanıcı ile temasınız oldu, belirti gösterirseniz test yaptırın.";

    public static final String SWITCH_ACTION_TITLE = "Bunun bir hata olmadığına emin misiniz?";

    public static final String TEST_RES_POSITIVE = "Test yaptırmak için en yakınınızdaki hastaneye başvurabilirsiniz.";

    public static final String TEST_RES_NEGATIVE = "Gayet iyi görünüyorsunuz, test yaptırmanıza gerek yok.";

    private static final String TITLE_1 = "Ateşiniz var mı?";

    private static final String BODY_1 = "38 derece üzeri ateş en belirgin Covid-19 belirtisidir.";

    private static final String TITLE_2 = "Burun akıntısı, halsizlik, vücut ağrınız var mı?";

    private static final String BODY_2 = "Sık görülen diğer Covid-19 belirtileri bunlardır.";

    private static final String TITLE_3 = "Nefes darlığınız var mı?";

    private static final String BODY_3 = "Corona virüs akciğerlerde tutuluma yol açarak, solunum yetmezliği, nefes darlığı ve zatürreye sebep " +
            "olabilir.";

    private static final String TITLE_4 = "Koku ve tat duyusu kaybı yaşıyor musunuz?";

    private static final String BODY_4 = "Corona virüs tat ve koku sinirlerini besleyen damarların uyarımında bozukluk yaşanmasına sebep olabilir.";

    private static final String TITLE_5 = "Son 1 ay içinde kalabalık ortamlarda bulundunuz mu?";

    private static final String BODY_5 = "Corona virüs genellikle temas sayesinde bulaşır.";

    private static final String TITLE_6 = "Yakın zamanda beslenme düzenininzde değişikliik oldu mu?";

    private static final String BODY_6 = "Beslenme düzeni değişikliği kendinizi halsiz hissetmenizi sağlayaiblir.";

    private static final String TITLE_7 = "Yakın zamanda uyku saatlerinizde değişiklik veya azalma oldu mu?";

    private static final String BODY_7 = "Uyku düzeni değişikliği kendinizi halsiz hissetmenizi sağlayabilir.";

    public static ArrayList<String> TEST_TITLES = new ArrayList<>(Arrays.asList(TITLE_1, TITLE_2, TITLE_3, TITLE_4, TITLE_5, TITLE_6, TITLE_7));

    public static ArrayList<String> TEST_BODIES = new ArrayList<>(Arrays.asList(BODY_1, BODY_2, BODY_3, BODY_4, BODY_5, BODY_6, BODY_7));

    public static String PRESERVATION_COVID_TITLE = "Nelere dikkat etmelisiniz?";

    public static String PRESERVATION_COVID_BODY = "► Ellerinizi en az 20 saniye boyunca sabun ve suyla yıkayın,\n sabun ve suyun olmadığı durumlarda " +
            "alkol bazlı el antiseptiği kullanın.\n\n► Ellerinizi yıkamadan ağız, burun ve gözlerinize temas etmeyin.\n\n► İnsanlarla aranıza en az 1.5 " +
            "metre mesafe koyun.\n\n► Ortak kullanılan nesneleri ve yüzeyleri dezenfekte ettiğinizden emin olun,\n havlu gibi kişisel eşyaları ortak " +
            "kullanmayın.\n\n► Seyahat sonrası 14 gün içinde herhangi bir solunum yolu semptomu olursa\n maske takarak en yakın sağlık kuruluşuna " +
            "başvurun.";

    public static String ADVICE_TITLE = "Size Önerilerimiz";

    public static String ADVICE_1 = "Covid-19 hasta sayısının yaklaşık %7'si ile aynı yaş grubundasınız.\n Demografik vaka dağılımında yaş grubunuz" +
            " son sıralarda. İstatistiklerde bu sıralamada kalmak için \nmaskenizi taktığınızdan emin olun ve kalabalık ortamlarda mümkün " +
            "olduğunca bulunmamaya çalışın.";

    public static String ADVICE_2 = "Covid-19 hasta sayısının yaklaşık %15'i ile aynı yaş grubundasınız.\n Demografik vaka dağılımında yaş " +
            "grubunuz 3. sırada. Corona virüs size ortalama bir risk teşkil ediyor. Dikkat ettiğinize eminiz ama yine de uyarmak istiyoruz, " +
            "maskenizi takmaya ve sosyal mesafe kurallarına uymaya devam edin.";

    public static String ADVICE_3 = "Covid-19 hasta sayısının yaklaşık %50'si ile aynı yaş grubundasınız.\n Demografik vaka dağılımında yaş " +
            "grubunuz ilk sırada.\n Hayatı durdurmanın mümkün olmadığını biliyoruz ancak en fazla dikkat etmesi gereken yaş grubundasınız," +
            "Sevdikleriniz ve geleceğiniz için sosyal mesafeye uyun, maske takmaya devam edin, olabildiğince izole olmaya çalışın.";

    public static String ADVICE_4 = "Covid-19 hasta sayısının yaklaşık %20'si ile aynı yaş grubundasınız.\n Demografik vaka dağılımında yaş " +
            "grubunuz 2. sırada.\n Olabildiğince dikkatli olmalısınız, sevdikleriniz ve geleceğiniz için sosyal mesafeye uyun ve maske takmaya " +
            "devam edin";

    public static String ADVICE_5 = "Covid-19 hasta sayısının yaklaşık %10'u ile aynı yaş grubundasınız.\n Demografik vaka dağılımında arka " +
            "sıralardasınız. Sosyal mesafe kurallarına uymaya ve maskenizi takmaya devam edin.";

    public static final String ADVICE_6 = "Covid-19 hasta sayısının yaklaşık %3'ü ile aynı yaş grubundasınız.\n Demografik vaka dağılımında yaş " +
            "grubunuz son sırada.\n Zaten dikkat ettiğinizi biliyoruz, bu şekilde devam edin ve kurallara uymayanları büyükleri olarak uyarın.";

    //Numbers goes
    public static final int BT_ACTIVITY_REQUEST_CODE = 101;

    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;

    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 3;
}
