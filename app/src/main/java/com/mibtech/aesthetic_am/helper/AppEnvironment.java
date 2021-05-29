package com.mibtech.aesthetic_am.helper;

public enum AppEnvironment {

    SANDBOX {
        @Override
        public String merchant_Key() {
            return Constant.MERCHANT_KEY;
        }

        @Override
        public String merchant_ID() {
            return Constant.MERCHANT_ID;
        }

        @Override
        public String furl() {
            return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        }

        @Override
        public String surl() {
            return "https://www.payumoney.com/mobileapp/payumoney/success.php";
        }

        @Override
        public String salt() {
            return Constant.MERCHANT_SALT;
        }

        @Override
        public boolean debug() {
            return true;
        }
    },

    PRODUCTION {
        @Override
        public String merchant_Key() {
            return Constant.MERCHANT_KEY;
        }

        @Override
        public String merchant_ID() {
            return Constant.MERCHANT_ID;
        }

        @Override
        public String furl() {
            return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        }

        @Override
        public String surl() {
            return "https://www.payumoney.com/mobileapp/payumoney/success.php";
        }

        @Override
        public String salt() {
            return Constant.MERCHANT_SALT;
        }

        @Override
        public boolean debug() {
            return false;
        }
    };


    public abstract String merchant_Key();

    public abstract String merchant_ID();

    public abstract String furl();

    public abstract String surl();

    public abstract String salt();

    public abstract boolean debug();


}
