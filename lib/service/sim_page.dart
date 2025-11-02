import 'package:flutter/material.dart';
import 'sim_info.dart'; // where you put the SimInfo class

class SimPage extends StatefulWidget {
  @override
  _SimPageState createState() => _SimPageState();
}

class _SimPageState extends State<SimPage> {
  List<Map<String, dynamic>> sims = [];

  @override
  void initState() {
    super.initState();
    _loadSims();
  }

  Future<void> _loadSims() async {
    final result = await SimInfo.getSubscriptionIds();
    setState(() => sims = result);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('SIM subscriptionIds')),
      body: ListView.builder(
        itemCount: sims.length,
        itemBuilder: (_, i) {
          final s = sims[i];
          return ListTile(
            title: Text('subId: ${s['subscriptionId']}'),
            subtitle: Text('carrier: ${s['carrierName'] ?? 'unknown'}  slot: ${s['simSlotIndex']} number:${s['number']}'),
          );
        },
      ),
    );
  }
}
