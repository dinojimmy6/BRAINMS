package constants;

import game.skill.Paladin;

public class Skill {

    public static boolean isKeyDownSkill(int id) {
        switch(id) {
            case 1311011: // la mancha spear
            case 2221011: // freezing breath
            case 2221052: // lightning orb
            case 2321001: // big bang
            case 3101008: // covering fire
            case 3111013: // arrow blaster
            case 3121020: // hurricane
            case 4341002: // final cut
            case 5221004: // rapid fire
            case 5221022: // broadside
            case 5311002: // monkey wave
//            case DemonAvenger.VitalityVeil:
//            case Phantom.MilleAiguilles:
//            case Shade.SpiritFrenzy:
//            case Shade.SpiritIncarnation:
//            case Luminous.SpectralLight:
//            case Jett.BackupBeatdown:
//            case Jett.BlasterBarrage:
//            case Jett.FallingStars:
//            case Jett.StarforceSalvo:
//            case Jett.VoltBarrage:
//            case DemonSlayer.SoulEater:
//            case DemonSlayer.CarrionBreath:
//            case DemonSlayer.GrimScythe:
            case 60011216: // soul buster
            case 65121003: // soul resonance
            case 80001587: // airship lv. 1
            case 80001389: // ?
            case 80001390: // ?
            case 80001391: // ?
            case 80001392: // ?
            case 80001629: // ?
            case 80001836: // vanquisher's charm
            case 80001880: // liberate the rune of barrage
            case 80001887: // mille aiguilles
            case 95001001: // flying battle chair mount
            case 101110100: // wheel wind
            case 131001004: // let's roll!
            case 131001008: // sky jump
            case 142111010: // kinetic jaunt
                return true;
            default:
                return false;
        }
    }

    public static boolean isSuperNovaSkill(int skillid) {
        switch(skillid) {
            case 4221052: // shadow veil
            case 65121052: // supreme supernova
                return true;
            default:
                return false;
        }
    }

    public static boolean isRushBombSkill(int skillid) {
        switch(skillid) {
//            case ILMage.FrozenOrb: // frozen orb
//            case Buccaneer.TornadoUppercut: // tornado uppercut
//            case Buccaneer.EnergyVortex:
//            case 12121001: // blazing extinction
//            case Luminous.MorningStar:
//            case Luminous.PressureVoid:
//            case DemonAvenger.BatSwarm:
            case 61111218: // wing beat
            case 101120200: // wind cutter
            case 101120203: // storm break
            case 101120205: // severe storm break
                return true;
            default:
                return false;
        }
    }

    public static boolean isDefaultedSkill(int skillId) {
        switch(skillId) {
            //case Hero.ComboAttack:
            case Paladin.ElementalCharge:
            //case DarkKnight.EvilEye:
            //case FpMage.ElementalDrain:
            //case FpMage.FerventDrain:
            //case ILMage.FreezingCrush:
            //case Bishop.BlessedEnsemble:
            //case Bowmaster.QuiverCartridge:
            //case Marksman.Rangefinder:
            //case NightLord.AssassinsMark:
            //case Shadower.CriticalGrowth:
            //case Buccaneer.EnergyCharge:
            //case Corsair.ScurvySummons:
            //case Shade.FoxTrot:
            //case Shade.SpiritBond1:
            //case Shade.FoxSpirit:
            //case Shade.CloseCall:
            //case Luminous.Sunfire:
            //case Luminous.Eclipse:
            //case Luminous.Equilibrium:
            //case Luminous.InnerLight:
            //case Luminous.FlashBlink:
            //case DemonAvenger.DemonicBlood:
            //case DemonAvenger.DarkWinds:
            //case DemonAvenger.DemonWings:
            //case DemonAvenger.DemonWings2:
            //case DemonAvenger.Exceed:
            //case DemonAvenger.BloodPact:
            //case Jett.RetroRockets:
            //case Kinesis.MentalShield:
                return true;
            default:
                return false;
        }
    }
}
