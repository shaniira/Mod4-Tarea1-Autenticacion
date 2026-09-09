// Respaldo de los usuarios y datos de prueba creados manualmente durante el
// desarrollo (NO incluye los datos demo automáticos de MongoDemoDataInitializer
// como "admin" recién creado, ana.demo@andina.local, POL-DEMO-*, etc. — esos se
// regeneran solos en cada arranque del backend contra una base de datos vacía).
//
// Uso — restaurar en cualquier momento (con el backend corriendo o no, mientras
// el contenedor de MongoDB "andina-clean-mongodb" esté arriba):
//
//   docker exec -i andina-clean-mongodb mongosh andina_seguros_clean < doc/respaldo-datos-prueba.js
//
// Es seguro ejecutarlo varias veces: usa upsert, así que no duplica nada si
// los documentos ya existen, y actualiza los campos si cambiaron.
//
// Generado: 2026-09-07 (Arquitectura-Clean, rama feature/auth-google-mfa)

// ---- usuarios ----

// admin: contraseña real "Admin123*" con MFA ya activado en este snapshot
// (mfaSecret real, escanear de nuevo con Google Authenticator si se restaura
// en una máquina distinta, porque el secreto solo sirve si ya lo escaneaste).
db.usuarios.updateOne(
  { _id: "00000000-0000-0000-0000-000000000001" },
  { $set: {
      username: "admin",
      passwordHash: "$2a$10$Zti3v52VS8SlXO2uC623s.5UxQD0dTgEstsznX33GJpcnZgqL/99C",
      rol: "ADMIN",
      activo: true,
      mfaSecret: "7BH5ABJOS42ER4QAUHAUPU4RSJSKUCNW",
      mfaHabilitado: true,
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.UsuarioDocument"
  }},
  { upsert: true }
);

// admin2 / admin2 — cuenta ADMIN de prueba sin MFA
db.usuarios.updateOne(
  { _id: "82fabab2-701f-4e97-8dfc-732243385915" },
  { $set: {
      username: "admin2",
      passwordHash: "$2a$10$KE5DOKlAEuzdqs5Y0pAkgemndjwZ0eKhPoqIZ8/PvquQZ5siFGGuK",
      rol: "ADMIN",
      activo: true,
      mfaHabilitado: false,
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.UsuarioDocument"
  }},
  { upsert: true }
);

// ramirezlisset361@gmail.com / Cliente123* — CLIENTE, vinculado a Google
db.usuarios.updateOne(
  { _id: "fb2dc04a-b94e-4664-a8d6-7b50583957b0" },
  { $set: {
      username: "ramirezlisset361@gmail.com",
      email: "ramirezlisset361@gmail.com",
      googleSubject: "106340085309020729868",
      rol: "CLIENTE",
      activo: true,
      passwordHash: "$2a$10$t61ahUeJHx5/f7sP0.KMQOArZS0zKVEYrJUDLvrdwv8gTfjLsDyJq",
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.UsuarioDocument"
  }},
  { upsert: true }
);

// shanira.2.rc@gmail.com / Agente123* — AGENTE, vinculado a Google
db.usuarios.updateOne(
  { _id: "88000000-0000-0000-0000-000000000001" },
  { $set: {
      username: "shanira.2.rc@gmail.com",
      email: "shanira.2.rc@gmail.com",
      googleSubject: "108041500550799639343",
      rol: "AGENTE",
      activo: true,
      passwordHash: "$2a$10$JSHnqduRdavS3oQXgVLQSuGV/CaoSeg/LxwtumVAxUaW6GOtvtb7m",
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.UsuarioDocument"
  }},
  { upsert: true }
);

// ---- cliente/vehículo/póliza/renovación de prueba de ramirezlisset361@gmail.com ----

db.clientes.updateOne(
  { _id: "99000000-0000-0000-0000-000000000001" },
  { $set: {
      tipoDocumento: "DNI",
      numeroDocumento: "87654321",
      nombres: "Lisset",
      apellidos: "Ramirez",
      fechaNacimiento: ISODate("1995-05-15"),
      correo: "ramirezlisset361@gmail.com",
      telefono: "921175206",
      activo: true,
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.ClienteDocument"
  }},
  { upsert: true }
);

db.vehiculos.updateOne(
  { _id: "99000000-0000-0000-0000-000000000002" },
  { $set: {
      clienteId: "99000000-0000-0000-0000-000000000001",
      placa: "ABC-999",
      marca: "Toyota",
      modelo: "Yaris",
      anioFabricacion: 2022,
      tipo: "AUTO",
      uso: "PARTICULAR",
      zonaCirculacion: "LIMA",
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.VehiculoDocument"
  }},
  { upsert: true }
);

db.polizas.updateOne(
  { _id: "99000000-0000-0000-0000-000000000004" },
  { $set: {
      numero: "POL-CLI-TEST-001",
      cotizacionId: "99000000-0000-0000-0000-000000000003",
      clienteId: "99000000-0000-0000-0000-000000000001",
      vehiculoId: "99000000-0000-0000-0000-000000000002",
      prima: 1200.00,
      moneda: "PEN",
      inicioVigencia: ISODate("2026-08-01"),
      finVigencia: ISODate("2027-02-01"),
      estado: "VIGENTE",
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.PolizaDocument"
  }},
  { upsert: true }
);

db.propuestas_renovacion.updateOne(
  { _id: "99000000-0000-0000-0000-000000000005" },
  { $set: {
      polizaOrigenId: "99000000-0000-0000-0000-000000000004",
      primaAnterior: 1200.00,
      nuevaPrima: 1260.00,
      porcentajeVariacion: 5.00,
      siniestrosConsiderados: 0,
      estado: "PENDIENTE",
      motivo: "Renovacion sin siniestros",
      creadaEn: new Date(),
      venceEn: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
      _class: "com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.RenovacionDocument"
  }},
  { upsert: true }
);

print("Respaldo restaurado: usuarios admin, admin2, ramirezlisset361@gmail.com, shanira.2.rc@gmail.com + datos de cliente de prueba.");
