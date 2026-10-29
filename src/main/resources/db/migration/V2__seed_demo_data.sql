-- ReTech Demonstration Reference Data
-- Seed initial collection centres and authorized recycling facilities

INSERT INTO collection_centres (name, address, city, postal_code, contact_phone, contact_email, supported_categories, operating_hours, is_active)
VALUES
('GreenTech Metro Collection Hub', '104 Silicon Valley Boulevard', 'Metro City', '560001', '+91-9876543210', 'hub.metro@retech-demo.org', 'LAPTOPS_COMPUTERS,MOBILE_TABLETS,ACCESSORIES_PERIPHERALS,CABLES_CHARGERS', 'Mon-Sat 09:00 - 18:00', TRUE),
('EcoDrop Community Centre', '45 Green Park Road, Sector 12', 'Metro City', '560034', '+91-9876543211', 'ecodrop@retech-demo.org', 'LAPTOPS_COMPUTERS,MOBILE_TABLETS,TELEVISIONS_DISPLAYS,BATTERIES_POWERBANKS', 'Mon-Sun 10:00 - 19:00', TRUE),
('Northside E-Waste Kiosk', '78 Industrial Area Phase 2', 'Riverdale', '560068', '+91-9876543212', 'northside@retech-demo.org', 'MOBILE_TABLETS,ACCESSORIES_PERIPHERALS,CABLES_CHARGERS,BATTERIES_POWERBANKS', 'Mon-Fri 08:30 - 17:30', TRUE),
('Central Recycling Depot', '12 Harbour Road, Dockside', 'Port City', '400001', '+91-9876543213', 'depot.port@retech-demo.org', 'LAPTOPS_COMPUTERS,TELEVISIONS_DISPLAYS,OTHER_ELECTRONICS', 'Tue-Sun 09:00 - 18:00', TRUE);

INSERT INTO recycling_facilities (name, address, city, postal_code, contact_phone, contact_email, capacity_kg_per_day, supported_materials, is_active)
VALUES
('Apex E-Waste Recovery Plant', 'Plot 50, Eco-Industrial Park', 'Metro City', '560100', '+91-9876543220', 'facility.apex@retech-demo.org', 2500.00, 'Printed Circuit Boards, Copper, Precious Metals, Lithium Cells, ABS Plastics', TRUE),
('PureEarth Smelting & Refining Works', 'Gate 3, CleanTech Zone', 'Riverdale', '560099', '+91-9876543221', 'facility.pureearth@retech-demo.org', 1500.00, 'Display Panels, Lead Glass, Aluminium, Batteries', TRUE),
('Coastal Material Reclamation Facility', 'Zone B, Port Logistic Hub', 'Port City', '400015', '+91-9876543222', 'facility.coastal@retech-demo.org', 3000.00, 'Computers, Power Supplies, Ferrous Metals, Heavy Electronics', TRUE);
