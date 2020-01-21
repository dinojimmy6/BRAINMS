/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package world;


import game.character.MapleCharacter;


public class PartyCharacter{

    private final String name;
    private final int id;
    private int level;
    private int channel;
    private int jobId;
    private int mapId;
    private final MapleCharacter chr;

    public PartyCharacter(MapleCharacter chr) {
        this.name = chr.getName();
        this.level = chr.getLevel();
        this.channel = chr.getChannel();
        this.id = chr.getId();
        this.jobId = chr.getJobId();
        this.mapId = chr.getMapId();
        this.chr = chr;
    }

    public PartyCharacter() {
        this.name = "";
        this.id = 0;
        this.chr = null;
}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getChannel() {
        return channel;
    }

    public boolean isOnline() {
        return channel >= 0;
    }

    public int getMapId() {
        if(channel >= 0) {
            return mapId;
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public void writePacket(byte[] packet, int channel, int mapId) {
        if(this.channel == channel && this.mapId == mapId) {
            chr.writePacket(packet);
        }
    }

    public void writePacket(byte[] packet) {
        chr.writePacket(packet);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PartyCharacter other = (PartyCharacter) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
