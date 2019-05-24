package com.linkedlogics.bio.dictionary;

public enum BioType {
    Integer((byte) 0),
    String((byte) 1),
    UtfString((byte) 2),
    Byte((byte) 3),
    Short((byte) 4),
    Boolean((byte) 5),
    Float((byte) 6),
    Long((byte) 7),
    Double((byte) 8),
    Time((byte) 9),
    JavaObject((byte) 10),
    Raw((byte) 11),
    BioObject((byte) 12),
    Alias((byte) 13),
    JavaEnum((byte) 15),
    BioEnum((byte) 16),
    Unknown((byte) 17),
    // starting from here these types are not serialized because they are dynamic and must be filled with actual values
    Dynamic((byte) -1),
    Conditional((byte) -1),
    Formatted((byte) -1);

    private byte value;

    private BioType(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static BioType getType(byte type) {
        switch (type) {
            case 0:
                return BioType.Integer;
            case 1:
                return BioType.String;
            case 2:
                return BioType.UtfString;
            case 3:
                return BioType.Byte;
            case 4:
                return BioType.Short;
            case 5:
                return BioType.Boolean;
            case 6:
                return BioType.Float;
            case 7:
                return BioType.Long;
            case 8:
                return BioType.Double;
            case 9:
                return BioType.Time;
            case 10:
                return BioType.JavaObject;
            case 11:
                return BioType.Raw;
            case 12:
                return BioType.BioObject;
            case 13:
                return BioType.Alias;
            case 15:
                return BioType.JavaEnum;
            case 16:
                return BioType.BioEnum;
            case 17:
                return BioType.Unknown;
        }
        return BioType.Unknown;
    }

    public enum MergedType {
        Max, Min, New, Old, And, Or, Append, Sum, Mix;
    }
}
