package services.and.receivers;

import java.util.ArrayList;

public class FieldChecker {
    public static String checkFields(ArrayList<FieldsList> fieldsList) {
        String value, key;
        for (int i = 0; i < fieldsList.size(); i++) {
            key = fieldsList.get(i).getKey();
            value = fieldsList.get(i).getValue();
            if (value.isEmpty()) {
                return "Lütfen tüm alanları doldurun";
            }
            switch (key) {
                case "name": {
                    if (!value.matches("(\\p{L}+\\s)+\\p{L}+")) {
                        return Constants.FIELD_CHECKER_NAME_ERR;
                    }
                    break;
                }

                case "mail": {
                    if (!value.contains("@") || value.contains(" ") || !value.contains(".")) {
                        return Constants.FIELD_CHECKER_MAIL_ERR;
                    }
                    break;
                }

                case "phone": {
                    if (value.length() != 13 || !value.matches("[+]\\p{N}*")) {
                        return Constants.FIELD_CHECKER_PHONE_ERR;
                    }
                    break;
                }

                case "birthday": {
                    break;
                }

                case "password": {
                    if (value.length() < 6 || value.contains(" ")) {
                        return Constants.FIELD_CHECKER_PASS_ERR;
                    }
                    break;
                }

                default: {
                    return Constants.FIELD_CHECKER_DEFAULT_ERR;
                }
            }
        }

        return "ok";
    }

    public static class FieldsList {
        private final String key, value;

        public FieldsList(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
