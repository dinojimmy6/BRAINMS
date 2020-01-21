package game.skill;

import game.character.MapleCharacter;
import game.map.MapleMap;
import utils.Logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmy on 10/14/2017.
 */
public class SkillManager {

    private static Map<Integer, Constructor<?>> buffHandlers = new HashMap<Integer, Constructor<?>>();
    private static Map<Integer, Constructor<?>> attackHandlers = new HashMap<Integer, Constructor<?>>();
    private static Map<Integer, Constructor<?>> staticBuffHandlers = new HashMap<Integer, Constructor<?>>();

    private static Class<?>[] skillHandlers = new Class<?>[]{
            Paladin.class,
            Hero.class,
    };

    public static void load() {
        try {
            for (Class<?> c : skillHandlers) {
                for (Class<?> inner : c.getDeclaredClasses()) {
                    SkillHandler annotation = inner.getAnnotation(SkillHandler.class);
                    if (annotation != null && (annotation.skillId() != 0 || annotation.skillIds().length != 0)) {
                        if(annotation.skillIds().length > 0) {
                            for(int skillId : annotation.skillIds()) {
                                if(annotation.skillType() == 0)
                                    buffHandlers.put(skillId, inner.getConstructor(int.class, MapleCharacter.class, MapleCharacter.class));
                                else
                                    attackHandlers.put(skillId, inner.getConstructor(int.class, int.class, MapleCharacter.class, MapleCharacter.class));
                            }
                        }
                        if (buffHandlers.containsKey(annotation.skillId()) && attackHandlers.containsKey(annotation.skillId())) {
                            Logging.log("Duplicate handler for skillId: " + annotation.skillId());
                        } else {
                            if(annotation.skillType() == 0) {
                                buffHandlers.put(annotation.skillId(), inner.getConstructor(int.class, MapleCharacter.class, MapleCharacter.class));
                                if (annotation.preregister())
                                    staticBuffHandlers.put(annotation.skillId(), inner.getConstructor(int.class, MapleCharacter.class, MapleCharacter.class));
                            }
                            else if(annotation.skillType() == 1)
                                attackHandlers.put(annotation.skillId(), inner.getConstructor(int.class, int.class, MapleCharacter.class, MapleCharacter.class));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logging.exceptionLog(e);
        }
        System.out.printf("A total of %s skills have been loaded.\r\n", buffHandlers.size() + attackHandlers.size());
    }

    private static boolean isValidMethod(Method method) {
        Class[] types = method.getParameterTypes();

        //return types.length == 2 && types[0].equals(MapleClient.class) && types[1].equals(LittleEndianAccessor.class);
        return true;
    }

    public static AttackSkillHandler manageAttack(int skillId, int slv, MapleCharacter chrFrom, MapleCharacter chrTo) {
        Constructor<?> constructor;
        try {
            constructor = attackHandlers.get(skillId);
            if (constructor != null) {
                return (AttackSkillHandler) constructor.newInstance(skillId, slv, chrFrom, chrTo);
            }
            else {
                return new AttackSkillHandler(skillId, slv, chrFrom);
            }

        } catch (InvocationTargetException e) {
            Logging.exceptionLog(e.getCause());
        } catch (Exception e) {
            Logging.exceptionLog(e);
        }
        return null;
    }

    public static void manageSpecialMove(int skillId, int slv, int channel, MapleCharacter chrFrom, MapleCharacter chrTo) {
        Constructor<?> constructor = buffHandlers.get(skillId);
        try {
            if (constructor != null) {
                constructor.newInstance(slv, chrFrom, chrTo);
            }
            else {
                BuffSkillHandler bsh = new BuffSkillHandler(skillId, slv, chrFrom, chrTo);
                bsh.registerEffect();
            }
        } catch (InvocationTargetException e) {
            Logging.exceptionLog(e.getCause());
        } catch (Exception e) {
            Logging.exceptionLog(e);
        }
    }

    public static void loadStaticBuffs(MapleCharacter chr) {
        try {
            for (Map.Entry<Integer, Constructor<?>> entry : staticBuffHandlers.entrySet()) {
                if (chr.cSkills.getSkillLevel(entry.getKey()) > 0) {
                    entry.getValue().newInstance(chr.cSkills.getSkillLevel(entry.getKey()), chr, chr);
                }
                else if(entry.getKey() == 1) {
                    entry.getValue().newInstance(chr.cSkills.getSkillLevel(entry.getKey()), chr, chr);
                }
            }
        } catch (InvocationTargetException e) {
            Logging.exceptionLog(e.getCause());
        } catch (Exception e) {
            Logging.exceptionLog(e);
        }
    }

    public static void loadStaticBuff(MapleCharacter chr, int skillId) {
        try {
            if(staticBuffHandlers.get(skillId) != null) {
                if (chr.cSkills.getSkillLevel(skillId) > 0) {
                    staticBuffHandlers.get(skillId).newInstance(chr.cSkills.getSkillLevel(skillId), chr, chr);
                }
            }
        } catch (Exception e) {
            Logging.exceptionLog(e.getStackTrace());
        }
    }
}