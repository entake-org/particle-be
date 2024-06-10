package io.sdsolutions.particle.smartystreets.services;

import java.util.HashMap;
import java.util.Map;

public class SmartyStreetsCodeMap {
    
    private static final Map<String, String> SS_CODES = new HashMap<>();
    
    public static Map<String, String> getCodes() {
        if(SS_CODES.isEmpty()) {
            SS_CODES.put("AA", "City/state/ZIP + street are all valid.");
            SS_CODES.put("A1", "ZIP+4 not matched; address is invalid. (City/state/ZIP + street don't match.)");
            SS_CODES.put("BB", "ZIP+4 matched; confirmed entire address; address is valid.");
            SS_CODES.put("CC", "Confirmed address by dropping secondary (apartment, suite, etc.) information.");
            SS_CODES.put("F1", "Matched to military or diplomatic address.");
            SS_CODES.put("G1", "Matched to general delivery address.");
            SS_CODES.put("M1", "Primary number (e.g., house number) is missing.");
            SS_CODES.put("M3", "Primary number (e.g., house number) is invalid.");
            SS_CODES.put("N1", "Confirmed with missing secondary information; address is valid but it also needs a secondary number (apartment, suite, etc.).");
            SS_CODES.put("PB", "Confirmed as a PO BOX street style address.");
            SS_CODES.put("P1", "PO, RR, or HC box number is missing.");
            SS_CODES.put("P3", "PO, RR, or HC box number is invalid.");
            SS_CODES.put("RR", "Confirmed address with private mailbox (PMB) info.");
            SS_CODES.put("R1", "Confirmed address without private mailbox (PMB) info.");
            SS_CODES.put("R7", "Confirmed as a valid address that doesn't currently receive US Postal Service street delivery.");
            SS_CODES.put("U1", "Matched a unique ZIP Code.");
            SS_CODES.put("A#", "Corrected ZIP Code");
            SS_CODES.put("B#", "Fixed city/state spelling");
            SS_CODES.put("C#", "Invalid city/state/ZIP");
            SS_CODES.put("D#", "No ZIP+4 assigned");
            SS_CODES.put("E#", "Same ZIP for multiple");
            SS_CODES.put("F#", "Address not found");
            SS_CODES.put("G#", "Used firm data");
            SS_CODES.put("H#", "Missing secondary number");
            SS_CODES.put("I#", "Insufficient/incorrect address data");
            SS_CODES.put("J#", "Dual address");
            SS_CODES.put("K#", "Cardinal rule match");
            SS_CODES.put("L#", "Changed address component");
            SS_CODES.put("LL#", "Flagged address for LACSLink");
            SS_CODES.put("LI#", "Flagged address for LACSLink");
            SS_CODES.put("M#", "Fixed street spelling");
            SS_CODES.put("N#", "Fixed abbreviations");
            SS_CODES.put("O#", "Multiple ZIP+4; lowest used");
            SS_CODES.put("P#", "Better address exists");
            SS_CODES.put("Q#", "Unique ZIP match");
            SS_CODES.put("R#", "No match; EWS: Match soon");
            SS_CODES.put("S#", "Bad secondary address");
            SS_CODES.put("T#", "Multiple response due to magnet street syndrome");
            SS_CODES.put("U#", "Unofficial post office name");
            SS_CODES.put("V#", "Unverifiable city / state");
            SS_CODES.put("W#", "Invalid delivery address");
            SS_CODES.put("X#", "Unique ZIP Code");
            SS_CODES.put("Y#", "Military match");
            SS_CODES.put("Z#", "Matched with ZIPMOVE");
        }

        return SS_CODES;
    }
}
