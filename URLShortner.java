import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class URLShortner {
    Map<String, String> shortenToOriginalMap;
    Map<String, String> originalToShortenMap;
    private static final String DEFAULT_ALIAS = "sid.ly";
    private static final String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 7;
    private static final String FORMAT = "%s://%s/%s";

    public URLShortner() {
        shortenToOriginalMap = new HashMap<>();
        originalToShortenMap = new HashMap<>();
    }

    // extract url
    // hash the url and convert to base 62
    private String shortenURL(String originalURL) {
        try {
            if(originalToShortenMap.containsKey(originalURL)) {
                return String.format(FORMAT,"http",DEFAULT_ALIAS,originalToShortenMap.get(originalURL));
            }
//            System.out.println("going");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(originalURL.getBytes());
            BigInteger hashInt = new BigInteger(1, hash);
            String encodeString = base62Encode(hashInt);
            String shortenString = encodeString.substring(0, Math.min(SHORT_URL_LENGTH, encodeString.length()));
            shortenToOriginalMap.put(shortenString,originalURL);
            originalToShortenMap.put(originalURL,shortenString);

            return String.format(FORMAT,"http",DEFAULT_ALIAS,shortenString);

        }
        catch(NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String getOriginalURL(String shortURL) {
        String shortenKey = getCodeFromUrl(shortURL);
        if(shortenToOriginalMap.containsKey(shortenKey)) {
            return shortenToOriginalMap.get(shortenKey);
        }
        return "no long url exist for this short url";
    }

    private String getCodeFromUrl(String url) {
        if(url == null || url.isEmpty()) {
            return "";
        }
        int lastIndex = url.lastIndexOf('/');
        if(lastIndex != -1 || lastIndex < url.length() -1) {
            return url.substring(lastIndex +1);
        }
        return "";
    }

    private String base62Encode(BigInteger num) {
        StringBuilder sb = new StringBuilder();
        while (num.compareTo(BigInteger.ZERO) > 0) {
           // BigInteger remainder = num.mod(BigInteger.valueOf(BASE62_CHARS.length()));
            BigInteger remainder = num.mod(BigInteger.valueOf(62));
            sb.insert(0,BASE62_CHARS.charAt(remainder.intValue()));


            num = num.divide(BigInteger.valueOf(62));

        }
        return sb.toString();
    }

    public static void main(String[] args) {
        URLShortner urlShortener = new URLShortner();
        String originalURL = "https://www.example.com/very/long/url/to/be/shortened";
        String shortURL = urlShortener.shortenURL(originalURL);
        System.out.println("Original URL: " + originalURL);
        System.out.println("Shortened URL: " + shortURL);
        String[] urlsToShorten = {
                "https://www.google.com/search?q=java+url+shortener",
                "https://en.wikipedia.org/wiki/URL_shortening",
                "https://github.com/google/guava",
                "https://stackoverflow.com/questions/742013/how-do-i-create-a-url-shortener",
                "https://stackoverflow.com/questions/742013/how-do-i-create-a-url-shortener?page=2", // A slightly different URL
                "https://stackoverflow.com/questions/742013/how-do-i-create-a-url-shortener?page=2"
        };
        for(String url : urlsToShorten){
            String shortUrl = urlShortener.shortenURL(url);
            System.out.println("Shortened URL: " + shortUrl);
            String retrievedUrl = urlShortener.getOriginalURL(shortUrl);
            System.out.println("Retrieved URL: " + retrievedUrl);
        }
        String retrievedURL = urlShortener.getOriginalURL(shortURL);
        System.out.println("Retrieved URL: " + retrievedURL);
    }
}