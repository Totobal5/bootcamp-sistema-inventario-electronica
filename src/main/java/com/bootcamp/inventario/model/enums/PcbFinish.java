package com.bootcamp.inventario.model.enums;

/**
 * Tipos de acabado superficial para PCBs
 * Define el recubrimiento de protección de las pads de cobre
 */
public enum PcbFinish {
    /**
     * Hot Air Solder Leveling - Nivelación por aire caliente
     * Más económico, bueno para soldadura manual
     */
    HASL,
    
    /**
     * Lead-Free HASL - HASL sin plomo (RoHS compliant)
     */
    LEAD_FREE_HASL,
    
    /**
     * Electroless Nickel Immersion Gold
     * Alta confiabilidad, superficie plana, ideal para BGAs
     */
    ENIG,
    
    /**
     * Immersion Silver - Plata por inmersión
     * Buen costo/beneficio, superficie plana
     */
    IMMERSION_SILVER,
    
    /**
     * Immersion Tin - Estaño por inmersión
     */
    IMMERSION_TIN,
    
    /**
     * Organic Solderability Preservative
     * Protección orgánica, económico
     */
    OSP,
    
    /**
     * Electroless Nickel Electroless Palladium Immersion Gold
     * Premium, para aplicaciones de alta confiabilidad
     */
    ENEPIG
}
