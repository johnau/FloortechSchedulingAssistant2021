package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import tech.jmcs.floortech.scheduling.app.datasource.model.BeamData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;

public class ObservableDataConverter {
    public static BeamDataObservable convert(BeamData beamData) {
        BeamDataObservable bdo = new BeamDataObservable();

        bdo.setBeamId(beamData.getBeamId());
        bdo.setBeamType(beamData.getBeamType());
        bdo.setLength(beamData.getLength());
        bdo.setQuantity(beamData.getQuantity());
        bdo.setTreatment(beamData.getTreatment());

        return bdo;
    }

    public static TrussDataObservable convert(TrussData trussData) {
        TrussDataObservable tdo = new TrussDataObservable();

        tdo.setAirConPeno(trussData.hasAirconPenetration());
        tdo.setLeftEndcap(trussData.getLeftEndcap());
        tdo.setRightEndcap(trussData.getRightEndcap());
        tdo.setLength(trussData.getLength());
        tdo.setPackingGroup(trussData.getPackingGroup());
        tdo.setPenetrationWebCuts(trussData.getPenetrationWebCuts());
        tdo.setQty(trussData.getQty());
        tdo.setTrussId(trussData.getTrussId());
        tdo.setType(trussData.getType());

        return tdo;
    }

    public static SlabDataObservable convert(SlabData slabData) {
        SlabDataObservable sdo = new SlabDataObservable();
        sdo.setMeasurementUnit(slabData.getMeasurementUnit());
        sdo.setName(slabData.getName());
        sdo.setSize(slabData.getSize());
        return sdo;
    }

}
