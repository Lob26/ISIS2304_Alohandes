const fs = require('fs');
const faker = require('faker');
const oracledb = require('oracledb');

async function run() {
    let connection;

    function extractDDL(sqlScript: string): { [key: string]: { [key: string]: string } }{
        const tableRegex = /CREATE\s+TABLE\s+(\w+)/gi;
        const variableRegex = /(\w+(\s*\w+)*)\s+(\w+(\(\d+\))?)/g;
        const tables: { [key: string]: { [key: string]: string } } = {};

        let matches;
        while ((matches = tableRegex.exec(sqlScript)) !== null) {
            const tableName = matches[1];
            const variableTypes: { [key: string]: string } = {};

            const tableDDLMatch = sqlScript.match(new RegExp(`CREATE\\s+TABLE\\s+${tableName}\\s*\\(([^;]+)\\);`, 'i'));

            if (tableDDLMatch) {
                const tableDDL = tableDDLMatch[1];
                let variableMatch;
                while ((variableMatch = variableRegex.exec(tableDDL)) !== null) {
                    const variableName = variableMatch[1];
                    const variableType = variableMatch[3];
                    variableTypes[variableName] = variableMatch[4] ? `${variableType}${variableMatch[4]}` : variableType;
                }
            }

            tables[tableName] = variableTypes;
        }
        return tables;
    }

    function extractFK(sqlScript: string): { [key: string]: string } {
        const foreignKeyRegex = /FOREIGN KEY\s*\(([^)]+)\)\s*REFERENCES\s*(\w+)\s*\(([^)]+)\)/gi;
        const foreignKeys: { [key: string]: string } = {};

        let matches;
        while ((matches = foreignKeyRegex.exec(sqlScript)) !== null) {
            const referencingColumns = matches[1].split(',').map(column => column.trim());
            const referencedTable = matches[2];
            const referencedColumns = matches[3].split(',').map(column => column.trim());

            for (let i = 0; i < referencingColumns.length; i++) {
                const referencingColumn = referencingColumns[i];
                const referencedColumn = referencedColumns[i];

                foreignKeys[referencingColumn] = `${referencedTable}.${referencedColumn}`;
            }
        }

        return foreignKeys;
    }

    function* generatePastDate() {
        let monthsAgo = 300;

        while (true) {
            const currentDate = new Date();
            yield new Date(currentDate.getFullYear(), currentDate.getMonth() - monthsAgo, 1);

            if (monthsAgo === -10) monthsAgo = 301;
            monthsAgo--;
        }
    }

    async function populateDatabase(tables: { [p: string]: { [p: string]: string } }, fks: { [p: string]: string }) {
        for (const [tabla, columnas] of Object.entries(tables)) {
            const registros = Array.from({length: 10}, () => {
                const registro: { [columna: string]: any } = {};

                for (const [columna, tipoVariable] of Object.entries(columnas)) {
                    let valor;
                    if (tipoVariable.includes('varchar') || tipoVariable.includes('varchar2'))
                        valor = faker.random.words(2);
                    else if (tipoVariable.includes('number'))
                        valor = tipoVariable.includes('1') ? faker.random.number({min: 0,max: 1}) : faker.random.number(10);
                    else if (tipoVariable === 'date' || tipoVariable === 'timestamp')
                        valor = generatePastDate();
                    registro[columna] = valor;
                }
                return registro;
            });
            await connection.insert(tabla, registros);
        }

        // Itera sobre el diccionario de claves foráneas
        for (const [tabla, tablaReferenciada] of Object.entries(fks)) {
            // Obtiene los IDs de los registros de la tabla referenciada
            const idsReferenciados = await connection.select(tablaReferenciada, ['id']);

            // Actualiza los registros en la tabla actual con claves foráneas aleatorias
            await connection.update(tabla, {
                columna_fk: () => faker.random.arrayElement(idsReferenciados).id,
            });
        }
    }

    try {
        const ddl = fs.readFileSync('../sql/ddl.sql', 'utf8');
        const dbConfig = JSON.parse(fs.readFileSync('config.json', 'utf8'));

        connection = await oracledb.getConnection(dbConfig);

        const tables: { [key: string]: { [key: string]: string } } = extractDDL(ddl);
        const foreignKeys: { [key: string]: string } = extractFK(ddl);

        populateDatabase(tables, foreignKeys);
    } catch (error) {
        console.error('Error al poblar la base de datos:', error);
    } finally {
        if (connection) await connection.close();
    }
}
