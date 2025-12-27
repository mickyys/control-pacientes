# Requisitos del Sistema para ControlPacientes

## Para Usuarios Finales

### Requisitos Mínimos

| Componente | Versión | Requerido | Nota |
|-----------|---------|----------|------|
| **Java** | 11 o superior | ✅ Sí | El instalador validará que esté instalado |
| Windows | 7 SP1 o superior | ✅ Sí | Windows 10/11 recomendado |
| RAM | 512 MB mínimo | ✅ Sí | 1 GB recomendado |
| Espacio en disco | 300 MB | ✅ Sí | Para la aplicación e datos |
| Maven | - | ❌ No | No es necesario |

### Instalación de Java (si es necesario)

Si el instalador detecta que no tienes Java instalado, hará lo siguiente:

#### Opción 1: Descargar Java desde Oracle (recomendado)
1. Ve a https://www.java.com/download
2. Descarga e instala la última versión de Java
3. Reinicia tu computadora
4. Vuelve a ejecutar el instalador de ControlPacientes

#### Opción 2: Instalar OpenJDK (alternativa gratuita)
1. Ve a https://adoptium.net/installation/
2. Descarga OpenJDK 19 o superior
3. Sigue el instalador
4. Vuelve a ejecutar el instalador de ControlPacientes

### Verificar que Java está instalado

Abre una terminal (cmd) y ejecuta:
```cmd
java -version
```

Deberías ver algo como:
```
java version "19.0.2" 2023-01-17
Java(TM) SE Runtime Environment (build 19.0.2+7-39)
Java HotSpot(TM) 64-Bit Server VM (build 19.0.2+7-39, mixed mode, sharing)
```

---

## Para Desarrolladores

### Requisitos de Desarrollo

| Componente | Versión | Uso |
|-----------|---------|-----|
| **Java JDK** | 19+ | Compilar el código |
| **Maven** | 3.8+ | Manejar dependencias y build |
| **Git** | 2.25+ | Control de versiones |
| IDE | IntelliJ IDEA / VS Code | Edición de código |

### Instalar Maven (solo desarrolladores)

#### Windows (con Chocolatey)
```powershell
choco install maven
```

#### Windows (manual)
1. Descarga Maven desde https://maven.apache.org/download.cgi
2. Extrae el archivo a `C:\Program Files\maven`
3. Agrega `C:\Program Files\maven\bin` a tu PATH
4. Verifica: `mvn -version`

#### macOS (con Homebrew)
```bash
brew install maven
```

#### Linux (Debian/Ubuntu)
```bash
sudo apt-get install maven
```

### Compilar la aplicación

```bash
git clone https://github.com/mickyys/control-pacientes
cd control-pacientes
mvn clean package
```

Esto generará el archivo compilado sin necesidad de Maven en el sistema del usuario final.

---

## Nota Importante

**Maven NO se incluye en el instalador MSI** porque:
- Los usuarios finales solo ejecutan la aplicación, no la compilan
- Maven es una herramienta de desarrollo, no de ejecución
- Esto mantiene el MSI ligero (~80 MB en lugar de ~150 MB)
- Los desarrolladores pueden instalar Maven según sea necesario

Si distribuyes ControlPacientes a usuarios finales, **solo necesitan Java instalado**.
