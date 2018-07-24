package org.bcia.julongchain.gossip.comm;

public class GroupDeMultiplexer {

    private Group[] groups;
    private Boolean closed;

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
