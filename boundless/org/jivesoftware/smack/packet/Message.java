package org.jivesoftware.smack.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jivesoftware.smack.util.StringUtils;

public class Message extends Packet {
    private final Set<Body> bodies = new HashSet();
    private String language;
    private final Set<Subject> subjects = new HashSet();
    private String thread = null;
    private Type type = Type.normal;

    public static class Body {
        private String language;
        private String message;

        private Body(String str, String str2) {
            if (str == null) {
                throw new NullPointerException("Language cannot be null.");
            } else if (str2 == null) {
                throw new NullPointerException("Message cannot be null.");
            } else {
                this.language = str;
                this.message = str2;
            }
        }

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
            Body body = (Body) obj;
            return this.language.equals(body.language) && this.message.equals(body.message);
        }

        public String getLanguage() {
            return this.language;
        }

        public String getMessage() {
            return this.message;
        }

        public int hashCode() {
            return ((this.language.hashCode() + 31) * 31) + this.message.hashCode();
        }
    }

    public static class Subject {
        private String language;
        private String subject;

        private Subject(String str, String str2) {
            if (str == null) {
                throw new NullPointerException("Language cannot be null.");
            } else if (str2 == null) {
                throw new NullPointerException("Subject cannot be null.");
            } else {
                this.language = str;
                this.subject = str2;
            }
        }

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
            Subject subject = (Subject) obj;
            return this.language.equals(subject.language) && this.subject.equals(subject.subject);
        }

        public String getLanguage() {
            return this.language;
        }

        public String getSubject() {
            return this.subject;
        }

        public int hashCode() {
            return ((this.language.hashCode() + 31) * 31) + this.subject.hashCode();
        }
    }

    public enum Type {
        normal,
        chat,
        groupchat,
        headline,
        error;

        public static Type fromString(String str) {
            try {
                return valueOf(str);
            } catch (Exception e) {
                return normal;
            }
        }
    }

    public Message(String str) {
        setTo(str);
    }

    public Message(String str, Type type) {
        setTo(str);
        this.type = type;
    }

    private String determineLanguage(String str) {
        String str2 = "".equals(str) ? null : str;
        return (str2 != null || this.language == null) ? str2 == null ? Packet.getDefaultLanguage() : str2 : this.language;
    }

    private Body getMessageBody(String str) {
        String determineLanguage = determineLanguage(str);
        for (Body body : this.bodies) {
            if (determineLanguage.equals(body.language)) {
                return body;
            }
        }
        return null;
    }

    private Subject getMessageSubject(String str) {
        String determineLanguage = determineLanguage(str);
        for (Subject subject : this.subjects) {
            if (determineLanguage.equals(subject.language)) {
                return subject;
            }
        }
        return null;
    }

    public Body addBody(String str, String str2) {
        Body body = new Body(determineLanguage(str), str2);
        this.bodies.add(body);
        return body;
    }

    public Subject addSubject(String str, String str2) {
        Subject subject = new Subject(determineLanguage(str), str2);
        this.subjects.add(subject);
        return subject;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message message = (Message) obj;
        if (!super.equals(message) || this.bodies.size() != message.bodies.size() || !this.bodies.containsAll(message.bodies)) {
            return false;
        }
        if (this.language != null) {
            if (!this.language.equals(message.language)) {
                return false;
            }
        } else if (message.language != null) {
            return false;
        }
        if (this.subjects.size() != message.subjects.size() || !this.subjects.containsAll(message.subjects)) {
            return false;
        }
        if (this.thread != null) {
            if (!this.thread.equals(message.thread)) {
                return false;
            }
        } else if (message.thread != null) {
            return false;
        }
        if (this.type != message.type) {
            z = false;
        }
        return z;
    }

    public Collection<Body> getBodies() {
        return Collections.unmodifiableCollection(this.bodies);
    }

    public String getBody() {
        return getBody(null);
    }

    public String getBody(String str) {
        Body messageBody = getMessageBody(str);
        return messageBody == null ? null : messageBody.message;
    }

    public Collection<String> getBodyLanguages() {
        Body messageBody = getMessageBody(null);
        Collection arrayList = new ArrayList();
        for (Body body : this.bodies) {
            if (!body.equals(messageBody)) {
                arrayList.add(body.language);
            }
        }
        return Collections.unmodifiableCollection(arrayList);
    }

    public String getLanguage() {
        return this.language;
    }

    public String getSubject() {
        return getSubject(null);
    }

    public String getSubject(String str) {
        Subject messageSubject = getMessageSubject(str);
        return messageSubject == null ? null : messageSubject.subject;
    }

    public Collection<String> getSubjectLanguages() {
        Subject messageSubject = getMessageSubject(null);
        Collection arrayList = new ArrayList();
        for (Subject subject : this.subjects) {
            if (!subject.equals(messageSubject)) {
                arrayList.add(subject.language);
            }
        }
        return Collections.unmodifiableCollection(arrayList);
    }

    public Collection<Subject> getSubjects() {
        return Collections.unmodifiableCollection(this.subjects);
    }

    public String getThread() {
        return this.thread;
    }

    public Type getType() {
        return this.type;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.thread != null ? this.thread.hashCode() : 0) + ((((this.type != null ? this.type.hashCode() : 0) * 31) + this.subjects.hashCode()) * 31)) * 31;
        if (this.language != null) {
            i = this.language.hashCode();
        }
        return ((hashCode + i) * 31) + this.bodies.hashCode();
    }

    public boolean removeBody(String str) {
        String determineLanguage = determineLanguage(str);
        for (Body body : this.bodies) {
            if (determineLanguage.equals(body.language)) {
                return this.bodies.remove(body);
            }
        }
        return false;
    }

    public boolean removeBody(Body body) {
        return this.bodies.remove(body);
    }

    public boolean removeSubject(String str) {
        String determineLanguage = determineLanguage(str);
        for (Subject subject : this.subjects) {
            if (determineLanguage.equals(subject.language)) {
                return this.subjects.remove(subject);
            }
        }
        return false;
    }

    public boolean removeSubject(Subject subject) {
        return this.subjects.remove(subject);
    }

    public void setBody(String str) {
        if (str == null) {
            removeBody("");
        } else {
            addBody(null, str);
        }
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setSubject(String str) {
        if (str == null) {
            removeSubject("");
        } else {
            addSubject(null, str);
        }
    }

    public void setThread(String str) {
        this.thread = str;
    }

    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<message");
        if (getXmlns() != null) {
            stringBuilder.append(" xmlns=\"").append(getXmlns()).append("\"");
        }
        if (this.language != null) {
            stringBuilder.append(" xml:lang=\"").append(getLanguage()).append("\"");
        }
        if (getPacketID() != null) {
            stringBuilder.append(" id=\"").append(getPacketID()).append("\"");
        }
        if (getTo() != null) {
            stringBuilder.append(" to=\"").append(StringUtils.escapeForXML(getTo())).append("\"");
        }
        if (getFrom() != null) {
            stringBuilder.append(" from=\"").append(StringUtils.escapeForXML(getFrom())).append("\"");
        }
        if (this.type != Type.normal) {
            stringBuilder.append(" type=\"").append(this.type).append("\"");
        }
        stringBuilder.append(">");
        Subject messageSubject = getMessageSubject(null);
        if (messageSubject != null) {
            stringBuilder.append("<subject>").append(StringUtils.escapeForXML(messageSubject.subject)).append("</subject>");
        }
        for (Subject subject : getSubjects()) {
            if (!subject.equals(messageSubject)) {
                stringBuilder.append("<subject xml:lang=\"").append(subject.language).append("\">");
                stringBuilder.append(StringUtils.escapeForXML(subject.subject));
                stringBuilder.append("</subject>");
            }
        }
        Body messageBody = getMessageBody(null);
        if (messageBody != null) {
            stringBuilder.append("<body>").append(StringUtils.escapeForXML(messageBody.message)).append("</body>");
        }
        for (Body body : getBodies()) {
            if (!body.equals(messageBody)) {
                stringBuilder.append("<body xml:lang=\"").append(body.getLanguage()).append("\">");
                stringBuilder.append(StringUtils.escapeForXML(body.getMessage()));
                stringBuilder.append("</body>");
            }
        }
        if (this.thread != null) {
            stringBuilder.append("<thread>").append(this.thread).append("</thread>");
        }
        if (this.type == Type.error) {
            XMPPError error = getError();
            if (error != null) {
                stringBuilder.append(error.toXML());
            }
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</message>");
        return stringBuilder.toString();
    }
}
