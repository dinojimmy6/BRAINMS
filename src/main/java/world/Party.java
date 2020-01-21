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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Party implements Serializable {
    private final int id;
    private PartyCharacter leader;
    private PartyCharacter pending;
    private final List<PartyCharacter> members = new LinkedList<>();

    public Party(int id, PartyCharacter leader) {
        this.leader = leader;
        this.members.add(this.leader);
        this.id = id;
    }

    public boolean containsMember(PartyCharacter member) {
        return members.contains(member);
    }

    public void addMember(PartyCharacter member) {
        members.add(member);
    }

    public void removeMember(PartyCharacter member) {
        members.remove(member);
    }

    public void updateMember(PartyCharacter member) {
        for (int i = 0; i < members.size(); i++) {
            PartyCharacter chr = members.get(i);
            if (chr.equals(member)) {
                members.set(i, member);
            }
        }
    }

    public PartyCharacter getMemberById(int id) {
        for (PartyCharacter chr : members) {
            if (chr.getId() == id) {
                return chr;
            }
        }
        return null;
    }

    public PartyCharacter getMemberByIndex(int index) {
        return members.get(index);
    }

    public synchronized Collection<PartyCharacter> getMembers() {
        return new LinkedList<>(members);
    }

    public int getId() {
        return id;
    }

    public PartyCharacter getLeader() {
        return leader;
    }

    public void setLeader(PartyCharacter nLeader) {
        leader = nLeader;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        final Party other = (Party) obj;
        return id == other.id;
    }

    public PartyCharacter getPending() {
        return pending;
    }

    public void setPending(PartyCharacter pending) {
        this.pending = pending;
    }
}
