package org.jivesoftware.smackx;

import java.util.List;
import org.jivesoftware.smackx.packet.MultipleAddresses;
import org.jivesoftware.smackx.packet.MultipleAddresses.Address;

public class MultipleRecipientInfo {
    MultipleAddresses extension;

    MultipleRecipientInfo(MultipleAddresses multipleAddresses) {
        this.extension = multipleAddresses;
    }

    public List<Address> getCCAddresses() {
        return this.extension.getAddressesOfType(MultipleAddresses.CC);
    }

    public Address getReplyAddress() {
        List addressesOfType = this.extension.getAddressesOfType(MultipleAddresses.REPLY_TO);
        return addressesOfType.isEmpty() ? null : (Address) addressesOfType.get(0);
    }

    public String getReplyRoom() {
        List addressesOfType = this.extension.getAddressesOfType(MultipleAddresses.REPLY_ROOM);
        return addressesOfType.isEmpty() ? null : ((Address) addressesOfType.get(0)).getJid();
    }

    public List<Address> getTOAddresses() {
        return this.extension.getAddressesOfType("to");
    }

    public boolean shouldNotReply() {
        return !this.extension.getAddressesOfType(MultipleAddresses.NO_REPLY).isEmpty();
    }
}
