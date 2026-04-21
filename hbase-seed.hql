# users
put 'users', 'admin', 'info:password', '123456'
put 'users', 'admin', 'info:role', 'ADMIN'
put 'users', 'user01', 'info:password', '123456'
put 'users', 'user01', 'info:role', 'USER'

# vehicles
put 'vehicles', 'vehicle_001', 'info:plateNumber', '沪A12345'
put 'vehicles', 'vehicle_001', 'info:brand', 'BYD'
put 'vehicles', 'vehicle_001', 'info:model', 'Han'
put 'vehicles', 'vehicle_001', 'info:ownerName', 'Zhang San'
put 'vehicles', 'vehicle_001', 'info:phone', '13800000000'
put 'vehicles', 'vehicle_001', 'info:status', 'ACTIVE'

put 'vehicles', 'vehicle_002', 'info:plateNumber', '沪B67890'
put 'vehicles', 'vehicle_002', 'info:brand', 'Tesla'
put 'vehicles', 'vehicle_002', 'info:model', 'Model 3'
put 'vehicles', 'vehicle_002', 'info:ownerName', 'Li Si'
put 'vehicles', 'vehicle_002', 'info:phone', '13900000000'
put 'vehicles', 'vehicle_002', 'info:status', 'MAINTENANCE'

# vehicle_audit
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:vehicleId', 'vehicle_001'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:action', 'CREATE'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:operator', 'admin'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:timestamp', '2026-04-20T19:00:00+08:00'

put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:vehicleId', 'vehicle_002'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:action', 'UPDATE_STATUS'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:operator', 'admin'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:timestamp', '2026-04-20T19:05:00+08:00'

# verify
scan 'users'
scan 'vehicles'
scan 'vehicle_audit'
