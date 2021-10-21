package tech.jmcs.floortech.scheduling.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.datasource.model.BeamData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SheetData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;
import tech.jmcs.floortech.scheduling.app.types.TrussEndCap;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class to store data extracted in an observable way for the DataFrame view and other data display views to use
 */
public class ExtractedDataHolderFX {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractedDataHolderFX.class);

    @Inject private SettingsHolder settingsHolder;

    private ObjectProperty<Date> lastUpdated;

    private Map<Long, BeamData> beamDataMap;
    private Map<Long, TrussData> trussDataMap;
//    private Map<Long, SheetData> sheetDataMap;
    private Map<Long, SlabData> slabDataMap;

    private Map<String, Map<Long, Map<String, Object>>> customData; // TODO: Ensure that (throughout the app) extractors have unique names.

    private StringProperty dataTarget;

    public ExtractedDataHolderFX() {
//        this.settings = settingsHolder;
        LOG.debug("ExtractedDataHolderFX...");
        this.lastUpdated = new SimpleObjectProperty<>();
        this.lastUpdated.set(new Date());

        this.beamDataMap = new HashMap<>();
        this.trussDataMap = new HashMap<>();
//        this.sheetDataMap = new HashMap<>();
        this.slabDataMap = new HashMap<>();
        this.customData = new HashMap<>();
    }

    public ExtractedDataHolderFX(SettingsHolder settingsHolder) {
        this();
        this.settingsHolder = settingsHolder;
    }

    public void setDataTarget(StringProperty dataTarget) {
        this.dataTarget = dataTarget;
    }

    public String getDataTarget() {
        return dataTarget.get();
    }

    public StringProperty dataTargetProperty() {
        return dataTarget;
    }

    public void clear() {
        if (this.beamDataMap != null) this.beamDataMap.clear();
        if (this.trussDataMap != null) this.trussDataMap.clear();
//        if (this.sheetDataMap != null) this.sheetDataMap.clear();
        if (this.slabDataMap != null) this.slabDataMap.clear();
        if (this.customData != null) this.customData.clear();
        this.setLastUpdated();
    }

    public Date getLastUpdated() {
        return lastUpdated.get();
    }

    public ObjectProperty<Date> lastUpdatedProperty() {
        return lastUpdated;
    }

    public void setLastUpdated() {
        this.lastUpdated.set(new Date());
    }

    public Map<Long, BeamData> getBeamDataMap() {
        return beamDataMap;
    }

    public void clearBeamDataMap() {
        if (this.beamDataMap != null) {
            this.beamDataMap.clear();
        }
    }

    public void addBeamData(Map<Long, BeamData> data) {
        for (Map.Entry<Long, BeamData> entry : data.entrySet()) {
            BeamData d = entry.getValue();
            addBeamData(d.getBeamId(), d.getBeamType(), d.getLength(), d.getQuantity(), d.getTreatment());
        }
        this.setLastUpdated();
    }

    public void addBeamData(String bId, String bType, Long bLen, Long bQty, BeamTreatment bTrmt) {
        long id = this.beamDataMap.size();
        BeamData bd = new BeamData();
        bd.setBeamId(bId);
        bd.setBeamType(bType);
        bd.setLength(bLen);
        bd.setQuantity(bQty);
        bd.setTreatment(bTrmt);
        if (bType.toLowerCase().contains("rhs")) {
            bd.setTreatmentLocked(true);
        }
        this.beamDataMap.put(id, bd);
    }

    public Map<Long, TrussData> getTrussDataMap() {
        return trussDataMap;
    }

    public void addTrussData(Map<Long, TrussData> data) {
        for (Map.Entry<Long, TrussData> entry : data.entrySet()) {
            TrussData d = entry.getValue();
            long id = this.trussDataMap.size();
            this.trussDataMap.put(id, d);
        }
        this.setLastUpdated();
    }

//    public Map<Long, SheetData> getSheetDataMap() {
//        return sheetDataMap;
//    }

//    public void addSheetData(Map<Long, SheetData> data) {
//        for (Map.Entry<Long, SheetData> entry : data.entrySet()) {
//            SheetData d = entry.getValue();
//            long id = this.sheetDataMap.size();
//            this.sheetDataMap.put(id, d);
//        }
//        this.setLastUpdated();
//    }

    public Map<Long, SlabData> getSlabDataMap() {
        return slabDataMap;
    }

    public void addSlabData(Map<Long, SlabData> data) {
        for (Map.Entry<Long, SlabData> entry : data.entrySet()) {
            SlabData d = entry.getValue();
            long id = this.slabDataMap.size();
            this.slabDataMap.put(id, d);
        }
        this.setLastUpdated();
    }

    public Map<String, Map<Long, Map<String, Object>>> getCustomData() {
        return customData;
    }

    public void addCustomData(String name, Map<Long, Map<String, Object>> customData) {
        if (this.customData == null) {
            this.customData = new HashMap<>();
        }
        this.customData.put(name, customData);
        this.setLastUpdated();
    }

    public void setCustomData(Map<String, Map<Long, Map<String, Object>>> customData) {
        this.customData.putAll(customData);
        this.setLastUpdated();
    }

    public Map<String, Object> convertToScheduleMap() {
        Map<String, Object> items = new HashMap<>();
        // combine similar items
        Map<String, Double> beamData = convertBeamData();
        Map<String, Double> trussData = convertTrussData();
        Map<String, Double> slabData = convertSlabData();
        Map<String, Double> sheetData = convertSheetData();

        // convert item names to spreadsheet name
        items.putAll(beamData);
        items.putAll(trussData);
        items.putAll(slabData);
        items.putAll(sheetData);
        return items;
    }

    private Map<String, Double> convertSheetData() {
        Map<String, Double> convertedSheetData = new HashMap<>();
//        Map<Long, Map<String, Object>>
        Map<Long, Map<String, Object>> sheetData = this.customData.get("Sheet");
        if (sheetData == null) {
            LOG.debug("There was no sheet data");
            return convertedSheetData;
        }

        for (Map.Entry<Long, Map<String, Object>> entry : sheetData.entrySet()) {
            Long id = entry.getKey();
            Map<String, Object> data = entry.getValue();

            StringBuilder sb = new StringBuilder();
            sb.append(id);
            sb.append(" -> ");
            data.forEach((n, d) -> {
                sb.append("{ '");
                sb.append(n);
                sb.append("' : '");
                sb.append(d.toString());
                sb.append("' [");
                sb.append(d.getClass().getSimpleName());
                sb.append("] } ");
            });
            LOG.info("Processing sheet data: #{}", sb.toString());

            Long len = 0l;
            Double qty = 0d;
            for (Map.Entry<String, Object> e : data.entrySet()) {
                String name = e.getKey();
                Object value = e.getValue();
                LOG.trace("Name: {} | Value: {} ({})", name, value, value.getClass().getSimpleName());
                boolean numeric = value instanceof Long || value instanceof Double || value instanceof Integer;
                boolean alphabetic = value instanceof String;
                boolean isLength = name.equalsIgnoreCase("length");
                boolean isQty = name.equalsIgnoreCase("quantity") || name.equalsIgnoreCase("qty");
                if (isLength && numeric) {
                    len = ((Double) value).longValue();
                    LOG.trace("Length: {}", len);
                } else if (isLength && alphabetic) {
                    try {
                        len = Long.parseLong((String) value);
                    } catch (NumberFormatException ex) {
                    }
                    LOG.trace("Length: {}", len);
                } else if (isQty && numeric) {
                    qty = (Double) value;
                    LOG.trace("Qty: {}", qty);
                } else if (isQty && alphabetic) {
                    try {
                        qty = Double.parseDouble((String) value);
                    } catch (NumberFormatException ex) {
                    }
                }
            }

            String convertedSheetType = convertSheetLength(len);
            LOG.trace("Converted Sheet Type: {}", convertedSheetType);
            Double finalQty = qty;
            convertedSheetData.computeIfPresent(convertedSheetType, (n, q) -> q + finalQty);
            convertedSheetData.putIfAbsent(convertedSheetType, qty);
        }
        LOG.debug("ConvertedSheetData:");
        convertedSheetData.forEach((n,d) -> {
            LOG.debug("Sheet: {} | Qty: {}", n, d);
        });
        return convertedSheetData;
    }

    private Map<String, Double> convertSlabData() {
        Map<String, Double> convertedSlabData = new HashMap<>();

        for (Map.Entry<Long, SlabData> entry : this.slabDataMap.entrySet()) {
            Long id = entry.getKey();
            SlabData data = entry.getValue();

            Double s = data.getSize();
            String convertedSlabType = convertSlabName(data.getName());

            String thinAngleName = this.settingsHolder.getScheduleEntrySlabThinAngle();
            String thickAngleName = this.settingsHolder.getScheduleEntrySlabThickAngle();
            if (convertedSlabType.equalsIgnoreCase(thinAngleName) || convertedSlabType.equalsIgnoreCase(thickAngleName)) {
                s = round45(s);
            }

            convertedSlabData.putIfAbsent(convertedSlabType, s);
        }

        return convertedSlabData;
    }

    private double round45(double x) {
        double f = Math.floor(x / 4.5) + 1;
        return f*4.5;
    }

    private Map<String, Double> convertBeamData() {
        Map<String, Double> convertedBeamData = new HashMap<>();
        for (Map.Entry<Long, BeamData> entry : this.beamDataMap.entrySet()) {
            Long id = entry.getKey();
            BeamData data = entry.getValue();
            double len = data.getQuantity() * data.getLength() / 1000D;

            String[] convertedBeamTypeNames = convertBeamName(data.getBeamType(), data.getTreatment());
            String convertedBeamType = "";
            int cnt = convertedBeamTypeNames.length;
            if (cnt > 0) {
                convertedBeamType = convertedBeamTypeNames[0];
            }
            convertedBeamData.computeIfPresent(convertedBeamType, (n, q) -> q + len);
            convertedBeamData.putIfAbsent(convertedBeamType, (double) len);

            if (cnt > 1) {
                for (int i = 1; i < cnt; i++) {
                    String cbtn = convertedBeamTypeNames[i];
                    convertedBeamData.computeIfPresent(cbtn, (n, q) -> q + len);
                    convertedBeamData.putIfAbsent(cbtn, (double) len);
                }
            }
        }
        return convertedBeamData;
    }

    private Map<String, Double> convertTrussData() {
        Map<String, Double> convertedTrussData = new HashMap<>();

        for (Map.Entry<Long, TrussData> entry : this.trussDataMap.entrySet()) {
            Long id = entry.getKey();
            TrussData data = entry.getValue();
            double len = data.getQty() * data.getLength() / 1000D;
            TrussEndCap lec = data.getLeftEndcap();
            TrussEndCap rec = data.getRightEndcap();
            LOG.debug("-------------- Truss ID: {} : LEFT : {}, RIGHT: {}", data.getTrussId(), lec, rec);
            int std = 0;
            int nec = 0;
            int adj = 0;
            if (lec == TrussEndCap.STANDARD) {
                std += data.getQty();
            } else if (lec == TrussEndCap.ADJUSTABLE) {
                adj += data.getQty();
                std += data.getQty();
            } else {
                nec += data.getQty();
            }
            if (rec == TrussEndCap.STANDARD) {
                std += data.getQty();
            } else if (rec == TrussEndCap.ADJUSTABLE) {
                adj += data.getQty();
                std += data.getQty();
            } else {
                nec += data.getQty();
            }

            LOG.debug("STD: {}, NEC: {}, ADJ: {}", std, nec, adj);

            List<Integer> pwc = data.getPenetrationWebCuts();
            Boolean hasPeno = data.hasAirconPenetration();
            pwc = pwc.stream().filter(f -> !f.equals(0)).collect(Collectors.toList());

            String convertedTrussForQuantity = convertTrussNameForQuantity(data.getType());
            String convertedTrussForLength = convertTrussNameForLength(data.getType());
            String convertedTrussForStdEndcaps = convertTrussNameForStdEndcaps(data.getType());
            String convertedTrussForNecEndcaps = convertTrussNameForNecEndcaps(data.getType());
            String convertedTrussForAcPeno = convertTrussNameForAcPeno(data.getType());
            String convertedTrussForAdjEndcaps = convertTrussNameForAdjEndCaps(data.getType());


//            String fixBrackets = "Fix brackets (inc tek screws)";

            convertedTrussData.computeIfPresent(convertedTrussForQuantity, (n, q) -> q + data.getQty());
            convertedTrussData.putIfAbsent(convertedTrussForQuantity, (double) data.getQty());

            convertedTrussData.computeIfPresent(convertedTrussForLength, (n, q) -> q + len);
            convertedTrussData.putIfAbsent(convertedTrussForLength, (double) len);

            // Handle Adjustable brackets as 1 Standard Bracket (With FIX Brackets) + 1 NEC Brackets (LARGE - 150mm) (Supply loose)
            // Handle Standard brackets as 1 Standard (With FIX BRACKETS)
            // Handle NEC Brackets as 1 UB Connection (With FIX BRACKETS)

            final int finalStd = std;
            final int finalNec = nec;
            final int finalAdj = adj;

            // Standard endcaps added up (includes adjustables)
            convertedTrussData.computeIfPresent(convertedTrussForStdEndcaps, (n, q) -> {
                LOG.debug("{}: {} incrementing STD from: {} to: {}", data.getTrussId(), convertedTrussForStdEndcaps, q, q+finalStd);
                return q + finalStd;
            });
            convertedTrussData.computeIfAbsent(convertedTrussForStdEndcaps, (n) -> {
                LOG.debug("{}: {} setting STD: {}", data.getTrussId(), convertedTrussForStdEndcaps, finalStd);
                return (double) finalStd;
            });

            // Fixed UB connection brackets added up (does not include adjustables)
            convertedTrussData.computeIfPresent(convertedTrussForNecEndcaps, (n, q) -> {
                LOG.debug("{}: {} incrementing UB EC from: {} to: {}", data.getTrussId(), convertedTrussForNecEndcaps, q, q+finalNec);
                return q + finalNec;
            });
            convertedTrussData.computeIfAbsent(convertedTrussForNecEndcaps, (n) -> {
                LOG.debug("{}: {} setting UB EC: {}", data.getTrussId(), convertedTrussForNecEndcaps, finalNec);
                return (double) finalNec;
            });

            // adjustables.. V
            convertedTrussData.computeIfPresent(convertedTrussForAdjEndcaps, (n, q) -> {
                LOG.debug("{}: {} incrementing ADJUSTABLE from: {} to: {}", data.getTrussId(), convertedTrussForAdjEndcaps, q, q+finalAdj);
                return q + finalAdj;
            });
            convertedTrussData.computeIfAbsent(convertedTrussForAdjEndcaps, n -> {
                LOG.debug("{}: {} setting Adjustable: {}", data.getTrussId(), convertedTrussForAdjEndcaps, finalAdj);
                return (double) finalAdj;
            });

            if (!pwc.isEmpty() || hasPeno) {
                convertedTrussData.computeIfPresent(convertedTrussForAcPeno, (n, q) -> q + (double) data.getQty());
                convertedTrussData.putIfAbsent(convertedTrussForAcPeno, (double) data.getQty());
            }
        }


        return convertedTrussData;
    }



    private String convertSheetLength(Long length) {
        String sheetSuffix = this.settingsHolder.getScheduleEntrySheetSuffix();
        Double ld = length / 1000d;
        return new DecimalFormat("0.0").format(ld) + sheetSuffix;
    }

    private String convertSlabName(String name) {
        String thinAngleName = this.settingsHolder.getScheduleEntrySlabThinAngle();
        String thickAngleName = this.settingsHolder.getScheduleEntrySlabThickAngle();
        String mainFloor = this.settingsHolder.getScheduleEntrySlabInternal();
        String rhs2cBalcony = this.settingsHolder.getScheduleEntrySlab2cRhs();
        String rhs3cBalcony = this.settingsHolder.getScheduleEntrySlab3cRhs();
        String insitu2cBalcony = this.settingsHolder.getScheduleEntrySlab2cInsitu();
        String insitu3cBalcony = this.settingsHolder.getScheduleEntrySlab3cInsitu();
        String insitu4cBalcony = this.settingsHolder.getScheduleEntrySlab4cInsitu();

        String nu = name.toUpperCase();
        if (nu.contains("BALCONY") && nu.contains("2C INSITU")) {
            return insitu2cBalcony;
        } else if (nu.contains("BALCONY") && nu.contains("3C INSITU")) {
            return insitu3cBalcony;
        } else if (nu.contains("BALCONY") && nu.contains("4C INSITU")) {
            return insitu4cBalcony;
        } else if (nu.contains("BALCONY") && nu.contains("2C RHS")) {
            return rhs2cBalcony;
        } else if (nu.contains("BALCONY") && nu.contains("3C RHS")) {
            return rhs3cBalcony;
        } else if (nu.contains("ANGLE") && nu.contains("THICK")) {
            return thickAngleName;
        } else if (nu.contains("ANGLE") && nu.contains("THIN")) {
            return thinAngleName;
        } else if (nu.contains("INTERNAL")) {
            return mainFloor;
        }
        return "";
    }

    private String convertTrussNameForQuantity(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return ""; // doesnt get used
        } else if (type.equalsIgnoreCase("CW346")) {
            return ""; // doesnt get used
        } else if (type.equalsIgnoreCase("HJ200")) {
            return this.settingsHolder.getScheduleEntryHj200TrussCount();
        } else if (type.equalsIgnoreCase("HJ300")) {
            return this.settingsHolder.getScheduleEntryHj300TrussCount();
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String convertTrussNameForLength(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return this.settingsHolder.getScheduleEntryCw260Truss();
        } else if (type.equalsIgnoreCase("CW346")) {
            return this.settingsHolder.getScheduleEntryCw346Truss();
        } else if (type.equalsIgnoreCase("HJ200")) {
            return this.settingsHolder.getScheduleEntryHj200Truss();
        } else if (type.equalsIgnoreCase("HJ300")) {
            return this.settingsHolder.getScheduleEntryHj300Truss();
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String convertTrussNameForStdEndcaps(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return this.settingsHolder.getScheduleEntryStandardEndcapsCw260();
        } else if (type.equalsIgnoreCase("CW346")) {
            return this.settingsHolder.getScheduleEntryStandardEndcapsCw346();
        } else if (type.equalsIgnoreCase("HJ200")) {
            return this.settingsHolder.getScheduleEntryStandardEndcapsHj200();
        } else if (type.equalsIgnoreCase("HJ300")) {
            return this.settingsHolder.getScheduleEntryStandardEndcapsHj300();
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String convertTrussNameForNecEndcaps(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return this.settingsHolder.getScheduleEntryConnectionEndcapsCw260();
        } else if (type.equalsIgnoreCase("CW346")) {
            return this.settingsHolder.getScheduleEntryConnectionEndcapsCw346();
        } else if (type.equalsIgnoreCase("HJ200")) {
            return this.settingsHolder.getScheduleEntryConnectionEndcapsHj200();
        } else if (type.equalsIgnoreCase("HJ300")) {
            return this.settingsHolder.getScheduleEntryConnectionEndcapsHj300();
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String convertTrussNameForAdjEndCaps(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return this.settingsHolder.getScheduleEntryAdjustableEndcapsCw260();
        } else if (type.equalsIgnoreCase("CW346")) {
            return this.settingsHolder.getScheduleEntryAdjustableEndcapsCw346();
        } else if (type.equalsIgnoreCase("HJ200")) {
            return this.settingsHolder.getScheduleEntryAdjustableEndcapsCw260();
        } else if (type.equalsIgnoreCase("HJ300")) {
            return this.settingsHolder.getScheduleEntryAdjustableEndcapsCw346();
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String convertTrussNameForAcPeno(String type) {
        if (type.equalsIgnoreCase("CW260")) {
            return this.settingsHolder.getScheduleEntryTrussAirConPenoCw260();
        } else if (type.equalsIgnoreCase("CW346")) {
            return this.settingsHolder.getScheduleEntryTrussAirConPenoCw346();
        } else if (type.equalsIgnoreCase("HJ200")) {
            return "Not Available (HJ200 Truss Penetration)";
        } else if (type.equalsIgnoreCase("HJ300")) {
            return "Not Available (HJ300 Truss Penetration)";
        } else {
            return "UNKNOWN TRUSS TYPE";
        }
    }

    private String[] convertBeamName(String beamName, BeamTreatment treatment) {

        // TODO - NEED TO HANDLE THE TBAR TOP HAT SEPARATELY

        Pattern ubUcPattern = Pattern.compile("([\\d]{2,3})[\\s]?([uU][bBcC])[\\s]?([\\d]{2,3})");
        Pattern tbarPattern = Pattern.compile("([Tt][-]?[Bb][Aa][Rr])[\\s]?([\\d]{2,3})[xX]([\\d]{1,2})[\\s]?[/][\\s]?([\\d]{2,3})[xX]([\\d]{1,2})[\\s]?[/]?[\\s]?([\\d]{2,3})?[xX]?([\\d]{1,2})?");
        Pattern pfcPattern  = Pattern.compile("([\\d]{2,3})[\\s]?([Pp][Ff][Cc])");
        Pattern anglePattern = Pattern.compile("([Aa][Nn][Gg][Ll][Ee])[\\s]([\\d]{2,3})[Xx]([\\d]{2,3})[Xx]([\\d]{1,3}[.]?[\\d]{0,2})");
        Pattern rhsPattern = Pattern.compile("([Rr][Hh][Ss])[\\s]?([\\d]{2,3})[Xx]([\\d]{2,3})[Xx]([\\d]{1,3}[.]?[\\d]{0,2})");
        Matcher ubucMatcher = ubUcPattern.matcher(beamName);
        Matcher tbarMatcher = tbarPattern.matcher(beamName);
        Matcher pfcMatcher = pfcPattern.matcher(beamName);
        Matcher angleMatcher = anglePattern.matcher(beamName);
        Matcher rhsMatcher = rhsPattern.matcher(beamName);
        boolean ubuc = ubucMatcher.matches();
        boolean tbar = tbarMatcher.matches();
        boolean pfc = pfcMatcher.matches();
        boolean angle = angleMatcher.matches();
        boolean rhs = rhsMatcher.matches();

        List<String> names = new ArrayList<>();

//        String cbn = "";
        String convertedTreatmentName = convertBeamTreatment(treatment);
        if (ubuc) {
            LOG.debug("Found UB/UC beam to convert: {}", beamName);
            names.add(String.format("%s %s %s %s", ubucMatcher.group(1), ubucMatcher.group(2), ubucMatcher.group(3), convertedTreatmentName));
        } else if (tbar) {
            LOG.debug("Found TBar beam to convert: {}", beamName);
            names.add(String.format("%sx%sBase / %sx%s Upstand - %s", tbarMatcher.group(2), tbarMatcher.group(3), tbarMatcher.group(4), tbarMatcher.group(5), convertedTreatmentName));
            if (tbarMatcher.group(6) != null && tbarMatcher.group(7) != null) {
                names.add(String.format("%sx%s TOP - E/O", tbarMatcher.group(6), tbarMatcher.group(7)));
            }
        } else if (pfc) {
            LOG.debug("Found PFC beam to convert: {}", beamName);
            names.add(String.format("%s PFC %s", pfcMatcher.group(1), convertedTreatmentName));
        } else if (angle) {
            LOG.debug("Found Angle beam to convert: {}", beamName);
            String thkDecimal = angleMatcher.group(4);
            Double d = 0d;
            try {
                d = Double.parseDouble(thkDecimal);
            } catch (NumberFormatException nex) {}
            String thk = new DecimalFormat("#").format(d);
            names.add(String.format("%s x %s x %s Lintel - %s", angleMatcher.group(2), angleMatcher.group(3), thk, convertedTreatmentName));
        } else if (rhs) {
            LOG.debug("Found RHS beam to convert: {}", beamName);
            names.add(String.format("%sx%sx%s RHS - %s", rhsMatcher.group(2), rhsMatcher.group(3), rhsMatcher.group(4), convertedTreatmentName));
        } else {
            LOG.warn("Unrecognized beam, not converted... {}", beamName);
        }

        LOG.debug("#: {}", names.toString());
        return names.toArray(new String[0]);
    }

    private String convertBeamTreatment(BeamTreatment treatment) {
        LOG.debug("Converting beam treatment: {}", treatment);
        String convertedTreatmentName = "";
        if (treatment == BeamTreatment.BLACK) {
            convertedTreatmentName = this.settingsHolder.getScheduleEntrySteelBlackKeyword();
        } else if (treatment == BeamTreatment.DIMET) {
            convertedTreatmentName = this.settingsHolder.getScheduleEntrySteelDimetKeyword();
        } else if (treatment == BeamTreatment.DURAGALV) {
            convertedTreatmentName = this.settingsHolder.getScheduleEntrySteelDuragalKeyword();
        } else if (treatment == BeamTreatment.EPOXY) {
            convertedTreatmentName = this.settingsHolder.getScheduleEntrySteelEpoxyKeyword();
        } else if (treatment == BeamTreatment.GALVANISED) {
            convertedTreatmentName = this.settingsHolder.getScheduleEntrySteelGalvanisedKeyword();
        }
        return convertedTreatmentName;
    }

    /**
     * Test method
     * @return
     */
    public String jsonifyData() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writerWithDefaultPrettyPrinter();
        try {
            String beamDataJson = ow.writeValueAsString(this.beamDataMap);
            String trussDataJson = ow.writeValueAsString(this.trussDataMap);
//            String sheetDataJson = ow.writeValueAsString(this.sheetDataMap);
            String slabDataJson = ow.writeValueAsString(this.slabDataMap);

            LOG.debug("Json Strings: {} \n {} \n {}", beamDataJson, trussDataJson, slabDataJson);
        } catch (JsonProcessingException e) {
            LOG.debug("Json exception: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Test method
     * @param jsonDatas
     */
    public void importJsonData(Map<String, String> jsonDatas) {
        String beamJson = jsonDatas.get("beam");
        String trussJson = jsonDatas.get("truss");
        String sheetJson = jsonDatas.get("sheet");
        String slabJson = jsonDatas.get("slab");

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<Long, BeamData>> beamJsonType = new TypeReference<HashMap<Long, BeamData>>() {};
        TypeReference<HashMap<Long, TrussData>> trussJsonType = new TypeReference<HashMap<Long, TrussData>>() {};
        TypeReference<HashMap<Long, SheetData>> sheetJsonType = new TypeReference<HashMap<Long, SheetData>>() {};
        TypeReference<HashMap<Long, SlabData>> slabJsonType = new TypeReference<HashMap<Long, SlabData>>() {};

        try {
            Map<Long, BeamData> beamMap = null;
            if (beamJson != null) beamMap = mapper.readValue(beamJson, beamJsonType);
            Map<Long, TrussData> trussMap = null;
            if (trussJson != null) trussMap = mapper.readValue(trussJson, trussJsonType);
            Map<Long, SheetData> sheetMap = null;
            if (sheetJson != null) sheetMap = mapper.readValue(sheetJson, sheetJsonType);
            Map<Long, SlabData> slabMap = null;
            if (slabJson != null) slabMap = mapper.readValue(slabJson, slabJsonType);

            if (beamMap != null) {
                beamMap.forEach((idx, data) -> {
                    LOG.debug("Beam data #{}: '{}', '{}', '{}', '{}', '{}'", idx, data.getBeamId(), data.getBeamType(), data.getLength(), data.getQuantity(), data.getTreatment());
                    if (data.getBeamType().toLowerCase().contains("rhs")) {
                        data.setTreatmentLocked(true);
                    }
                });
                this.beamDataMap.clear();
                this.beamDataMap.putAll(beamMap);
            }
            if (trussMap != null) {
                trussMap.forEach((idx, data) -> {
                    LOG.debug("Truss data #{}: '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}'", idx, data.getTrussId(), data.getType(), data.getQty(), data.getLength(), data.getLeftEndcap(), data.getRightEndcap(), data.getPenetrationWebCuts(), data.getPackingGroup());
                });
                this.trussDataMap.clear();
                this.trussDataMap.putAll(trussMap);
            }
            if (sheetMap != null) {
                LOG.warn("Processing of sheet map is not yet implemented");
            }
            if (slabMap != null) {
                LOG.warn("Processing of slab map is not yet implemented");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    //
//    public List<Map<Long, Map<String, Object>>> getCustomDataList() {
//        return customDataList;
//    }
//
//    public void addCustomData(Map<Long, Map<String, Object>> data) {
//        if (customDataList == null) {
//            customDataList = new ArrayList<>();
//        }
//        this.customDataList.add(data);
//        this.setLastUpdated();
//    }
//
//    public void setCustomDataList(List<Map<Long, Map<String, Object>>> customDataList) {
//        this.customDataList = customDataList;
//        this.setLastUpdated();
//    }

}
