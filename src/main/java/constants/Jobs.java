package constants;

public class Jobs {

    public static final boolean enableJobs = true;
    public static final int jobOrder = 8;

    public enum LoginJob {
        RESISTANCE(0, JobFlag.DISABLED, 3000, 931000000, true, false, false, false, false, false),
        EXPLORER(1, JobFlag.ENABLED, 0, 4000011, false, true, false, false, false, false),
        CYGNUS(2, JobFlag.DISABLED, 1000, 130030000, false, true, false, false, false, true),
        ARAN(3, JobFlag.DISABLED, 2000, 914000000, true, true, false, false, true, false),
        EVAN(4, JobFlag.DISABLED, 2001, 900010000, true, true, false, false, true, false),
        MERCEDES(5, JobFlag.DISABLED, 2002, 910150000, false, false, false, false, false, false),
        DEMON(6, JobFlag.ENABLED, 3001, 931050310, false, false, true, false, false, false),
        PHANTOM(7, JobFlag.DISABLED, 2003, 915000000, false, true, false, false, false, true),
        DUAL_BLADE(8, JobFlag.ENABLED, 0, 103050900, false, true, false, false, false, false),
        MIHILE(9, JobFlag.DISABLED, 5000, 913070000, true, true, false, false, true, false),
        LUMINOUS(10, JobFlag.ENABLED, 2004, 910141110, false, true, false, false, false, true),
        KAISER(11, JobFlag.DISABLED, 6000, 940001000, false, true, false, false, false, false),
        ANGELIC(12, JobFlag.DISABLED, 6001, 940011000, false, true, false, false, false, false),
        CANNONEER(13, JobFlag.DISABLED, 0, 3000000, true, true, false, false, true, false),
        XENON(14, JobFlag.DISABLED, 3002, 931050920, true, true, true, false, false, false),
        ZERO(15, JobFlag.DISABLED, 10112, 321000000, false, true, false, false, false, true),
        SHADE(16, JobFlag.ENABLED, 2500, 927030000, false, true, false, false, true, true),
        JETT(17, JobFlag.ENABLED, 0, 552000050, false, false, false, false, false, true),
        HAYATO(18, JobFlag.DISABLED, 4001, 807000000, true, true, false, true, false, false),
        KANNA(19, JobFlag.DISABLED, 4002, 807040000, true, true, false, true, false, false),
        CHASE(20, JobFlag.DISABLED, 4002, 807040000, true, true, false, true, false, false),
        PINK_BEAN(21, JobFlag.DISABLED, 4002, 807040000, true, true, false, true, false, false),
        KINESIS(22, JobFlag.ENABLED, 14000, 910141020, false, true, false, false, false, false);

        public final int jobType, flag, id, map;
        public boolean hairColor, skinColor, faceMark, hat, bottom, cape;
        private LoginJob(int jobType, JobFlag flag, int id, int map, boolean hairColor, boolean skinColor, boolean faceMark, boolean hat, boolean bottom, boolean cape) {
            this.jobType = jobType;
            this.flag = flag.getFlag();
            this.id = id;
            this.map = map;
            this.hairColor = hairColor;
            this.skinColor = skinColor;
            this.faceMark = faceMark;
            this.hat = hat;
            this.bottom = bottom;
            this.cape = cape;
        }

        public int getJobType() {
            return jobType;
        }

        public int getFlag() {
            return flag;
        }

        public enum JobFlag {

            DISABLED(0),
            ENABLED(1);
            private final int flag;

            private JobFlag(int flag) {
                this.flag = flag;
            }

            public int getFlag() {
                return flag;
            }
        }

        public static LoginJob getByLoginId(int g) {
            if (g == LoginJob.CANNONEER.jobType) {
                return LoginJob.EXPLORER;
            }
            for (LoginJob e : LoginJob.values()) {
                if (e.jobType == g) {
                    return e;
                }
            }
            return null;
        }

        public static LoginJob getByJobId(int g) {
            if (g == LoginJob.EXPLORER.id) {
                return LoginJob.EXPLORER;
            }
            if (g == 508) {
                return LoginJob.JETT;
            }
            for (LoginJob e : LoginJob.values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return null;
        }
    }

    public enum Job {
        BEGINNER(0, 0),
        Warrior(100, 1),
        Fighter(110, 2),
        Crusader(111, 3),
        Hero(112, 4),
        Page(120, 2),
        WhiteKnight(121, 3),
        Paladin(122, 4),
        MAGICIAN(200, 1),
        ILWIZARD(220, 2),
        ILMAGE(221, 3),
        ILARCHMAGE(222, 4);

        private final int jobId, rank;

        private Job(int jobId, int rank) {
            this.jobId = jobId;
            this.rank = rank;
        }

        public static boolean isSeparatedSp(Job job) {
            return true;
        }

        public int getRank() {
            return rank;
        }

        public int getJobId() {
            return jobId;
        }

        public static Job getJobById(int id) {
            for(Job j : Job.values()) {
                if(j.getJobId() == id) {
                    return j;
                }
            }
            return null;
        }

        public static boolean isMage(int job) {
            return (job >= 200 && job <= 232) || (job >= 1200 && job <= 1212) || (job >= 2200 && job <= 2218) || (job >= 3200 && job <= 3212);
        }
    }
}
