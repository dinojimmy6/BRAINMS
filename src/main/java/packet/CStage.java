package packet;

import constants.Flags;
import constants.Flags.CharFlags;
import game.character.MapleCharacter;
import utils.Randomizer;
import utils.data.PacketWriter;

public class CStage {
    public static byte[] getWarpToMap(MapleCharacter mc, int channel, int mapId, int spawnPoint, boolean bCharacterData) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SET_FIELD.getValue());

        // size (int + int)
        pw.writeShort(0);

        pw.writeInt(channel);

        // bDev
        pw.write(0);

        // wOldDriverID
        pw.writeInt(0);

        // Are you logging into the handling? (1), or changing the map? (2)
        // bPopupDlg
        pw.write(bCharacterData ? 1 : 2);

        // ?
        pw.writeInt(0);

        // nFieldWidth
        pw.writeInt(800);

        // nFieldHeight
        pw.writeInt(600);

        // Are you logging into the handling? (1), or changing the map? (0)
        pw.write(bCharacterData);

        // size (string (size->string))
        pw.writeShort(0);

        if(bCharacterData) {
            mc.writeSeeds(pw);
            addCharacterInfo(pw, mc);
            pw.writeInt(0); // log out event
        } else {

            // bUsingBuffProtector (this will call the revive function, upon death.)
            pw.write(0);

            pw.writeInt(mapId);
            pw.write(spawnPoint);
            pw.writeInt(mc.stats.getLast().hp);

            // (bool (int + int))
            pw.write(0);
        }

        // set white fade in-and-out
        pw.write(0);

        // set overlapping screen animation
        pw.write(0);

        // some sort of korean event fame-up
        pw.writeLong(0);

        // ?
        pw.writeInt(0x64);

        // party map experience.
        // bool (int + string(bgm) + int(fieldid))
        pw.write(0);

        // bool
        pw.write(0);

        // ?
        pw.write(1);

        // bool (int)
        pw.write(0);

        // bool ((int + byte(size))->(int, int, int))->(long, int, int)
        pw.write(0);

        // bool (int + byte + long)

        pw.write(0);

        // int(size)->(int, string)
        pw.writeInt(0);

        // FreezeHotEventInfo

        // nAccountType
        pw.write(0);

        // dwAccountID
        pw.writeInt(0);

        // EventBestFriendInfo

        // dwEventBestFriendAID
        pw.writeInt(0);

        pw.writeInt(0);

        return pw.getPacket();
    }

    public static void addCharacterInfo(PacketWriter pw, MapleCharacter chr) {
        long mask = CharFlags.defaultMask();
        pw.writeLong(mask);

        // combat orders
        pw.write(0);

        // pet active skill cool time
        for (int i = 0; i < 3; i++) {
            pw.writeInt(0);
        }

        pw.write(0);

        pw.write(0);

        pw.writeInt(0);

        // ?
        pw.write(0);

        if(CharFlags.checkMask(mask, CharFlags.CHARSTATS)) {
            chr.writeGWCharacterStats(pw);

            pw.write(5);

            pw.write(0);
//            if (chr.getBlessOfFairyOrigin() != null) {
//                pw.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
//            }

            pw.write(0);
//            if (chr.getBlessOfEmpressOrigin() != null) {
//                pw.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
//            }

//            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
            pw.write(0);
//            if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
//                pw.writeMapleAsciiString(ultExplorer.getCustomData());
//            }
        }

        if(CharFlags.checkMask(mask, CharFlags.MESO)) {
            pw.writeLong(100);
        }
        if(CharFlags.checkMask(mask, CharFlags.ITEMSLOTUSE) || CharFlags.checkMask(mask, CharFlags.EXPCONSUMEITEM)) {
            pw.writeInt(0);
        }
        if(CharFlags.checkMask(mask, CharFlags.ITEMSLOTUSE) || CharFlags.checkMask(mask, CharFlags.UNK1)) {
            pw.writeInt(0);
        }
        if(CharFlags.checkMask(mask, CharFlags.ITEMSLOTSIZE)) {
            pw.write(32);
            pw.write(32);
            pw.write(32);
            pw.write(32);
            pw.write(32);
        }
        if(CharFlags.checkMask(mask, CharFlags.CHARSKIN)) {
            pw.writeInt(0);
            pw.writeInt(0);
        }
        if(CharFlags.checkMask(mask, CharFlags.ITEMSLOTEQUIP)) {
            pw.write(0);
            chr.equips.addInfo(pw, chr);
        }

        //do rest of invs here

        if(CharFlags.checkMask(mask, CharFlags.COREAURA)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.SHOPBUYLIMIT)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.ITEMPOT)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.SKILL)) {
            chr.cSkills.writeSkillInfo(pw);
        }

        if(CharFlags.checkMask(mask, CharFlags.COOLDOWN)) {
            //addCoolDownInfo(pw, chr);
        }

        if(CharFlags.checkMask(mask, CharFlags.QUEST)) {
            //addStartedQuestInfo(pw, chr);
        }

        if(CharFlags.checkMask(mask, CharFlags.COMPLETEDQUEST)) {
            //addCompletedQuestInfo(pw, chr);
        }

        if(CharFlags.checkMask(mask, CharFlags.MINIGAME)) {
            pw.writeShort(0);
        }
        if(CharFlags.checkMask(mask, CharFlags.COUPLE)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.HYPERROCK)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.MONSTERBOOKCOVER)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.MONSTERBOOK)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.FAMILIAR)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.QUESTEX)) {
            pw.writeShort(0);
        }

        if(CharFlags.checkMask(mask, CharFlags.AVATAR)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.JAGUAR)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.ZERO)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.SHOPBUYLIMIT)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.STOLENSKILLS)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.CHOSENSKILLS)) {
        }

        if(CharFlags.checkMask(mask, CharFlags.INNERABILITY)) {
        }

        pw.writeInt(0);
        pw.write(0);

        if(CharFlags.checkMask(mask, CharFlags.HONOR)) {
        }

//        if(CharFlags.checkMask(mask, CharFlags.MESO)) {
//            pw.write(1);
//            pw.writeShort(0);
//        }

        if(CharFlags.checkMask(mask, CharFlags.ANGELICBUSTER)) {
            pw.writeInt(21173); //face
            pw.writeInt(37141); //hair
            pw.writeInt(1051291);
            pw.write(0);
            pw.writeInt(-1);
            pw.writeInt(0);
            pw.writeInt(0);
        }

        pw.writeShort(0);
        pw.write(0); // m_bFarmOnline
        pw.writeInt(0); // DecodeTextEquipInfo
    }
}
