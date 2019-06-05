//package ru.lodes.lincore.commands;
//
//import ru.lodes.lincore.UpWorldProxy;
//import ru.lodes.lincore.api.command.CommandSender;
//import ru.lodes.lincore.api.enums.CommandAccess;
//import ru.lodes.lincore.api.modules.ICommand;
//import ru.lodes.lincore.network.packets.bungee.TestMSGPacket;
//
//public class TestCommand extends ICommand {
//
//    public TestCommand(UpWorldProxy core) {
//        super(core.getPlugin(), CommandAccess.CORE, "test", "");
//    }
//
//    @Override
//    public void onCheckedCommand(CommandSender sender, String[] args) {
//        getCore().getDataHandler().getProxysMap().values().forEach(srv -> {
//            srv.sendPacket(new TestMSGPacket());
//        });
//    }
//}
