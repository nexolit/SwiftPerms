# EzPerms
Easy to use permission plugin for Bukkit/Spigot servers.

**Do you want to set up permissions on your SMP,
but don't want to bother with setting up too long ?**

EzPerms might be the right thing for you!

It supports setting permissions for groups of players or to the player alone.

This is a project I made a while ago, and decided to update it and make it public.
A simple permission plugin for minecraft servers.
> [!NOTE]
> This plugin is compatible with [Vault](https://dev.bukkit.org/projects/vault) Permissions.
> 
> It is recommended to install [Vault](https://dev.bukkit.org/projects/vault) to ensure compatibility with some plugins like WorldEdit.

> [!IMPORTANT]
> This plugin wasn't made with big servers in mind and may cause issues.

## USAGE
You can manage permissions through the console or the config files.

You can also easily change configuration in the EzPerms folder
located in the Plugins folder conveniently using the JSON format.
You should run this command after changing the config directly
to reflect changes on a running server.
```console
/perms refresh
```

### Creating groups
```console
/group add <group_name>
```

### Removing groups
```console
/group remove <group_name>
```

### Adding a player to group
```console
/group player add <group_name> <player_name>
```

### Removing a player from group
```console
/group player remove <group_name> <player_name>
```

## PERMISSIONS
You can find the permissions for the groups in the **groups.json** file.
And the permissions for the players in the **players.json**

To give players access to the permissions,
you can give them these permissions.
> **ezperms.group** - for managing groups
> 
> **ezperms.permissions** - for managing permissions

This plugin also supports wildcards that allow you to set all 
the subpermissions of a permission.
### Adding a group permission
```console
/perms group <group_name> add <permission>
```

### Removing a group permission
```console
/perms group <group_name> remove <permission>
```

### Adding a player permission
```console
/perms player <player_name> add <permission>
```

### Removing a player permission
```console
/perms player <player_name> remove <permission>
```

## METRICS
>[!NOTE]
> This plugin utilizes the bStats metrics system, which means that the following information is collected and sent to bstats.org:

-- PLUGIN SPECIFIC INFORMATION --
* Group count
* Permission count
* Vault usage
* Download source

-- GENERIC INFORMATION --
* A unique identifier
* The server's version of Java
* Whether the server is in offline or online mode
* The plugin's version
* The server's version
* The OS version/name and architecture
* The core count for the CPU
* The number of players online
* The Metrics version

Opting out of this service can be done by editing plugins/bstats/config.yml and changing opt-out to true.

## BUILDING
### Windows
**through CMD:**
```console
build
```
**through PowerShell:**
```powershell
.\build.cmd
```

### MacOS / Linux / Unix
```console
./build.sh
```
