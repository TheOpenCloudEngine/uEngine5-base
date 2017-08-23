/**
 * TypedJsonAdaptor JsonDefinition 변환기
 *
 * @class
 *
 * @author <a href="mailto:sppark@uengine.org">Seungpil Park</a>
 */
var TypedJsonAdaptor = function () {
  this.json = null;

  this.types = {
    /**
     * Start Event
     */
    startEvent: {
      java: 'org.uengine.kernel.bpmn.StartEvent',
      javaView: 'org.uengine.kernel.bpmn.view.StartEventView',
      shapeId: 'OG.shape.bpmn.E_Start'
    },
    messageStartEvent: {
      java: 'org.uengine.kernel.bpmn.StartEvent',
      javaView: 'org.uengine.kernel.bpmn.view.StartEventView',
      shapeId: 'OG.shape.bpmn.E_Start_Message'
    },
    timerStartEvent: {
      java: 'org.uengine.kernel.bpmn.StartEvent',
      javaView: 'org.uengine.kernel.bpmn.view.StartEventView',
      shapeId: 'OG.shape.bpmn.E_Start_Timer'
    },
    conditionalStartEvent: {
      java: 'org.uengine.kernel.bpmn.StartEvent',
      javaView: 'org.uengine.kernel.bpmn.view.StartEventView',
      shapeId: 'OG.shape.bpmn.E_Start_Rule'
    },
    signalStartEvent: {
      java: 'org.uengine.kernel.bpmn.StartEvent',
      javaView: 'org.uengine.kernel.bpmn.view.StartEventView',
      shapeId: 'OG.shape.bpmn.E_Start_Signal'
    },

    /**
     * Intermediate Event
     */
    intermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    messageIntermediateCatchEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate_Message'
    },
    messageIntermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate_Message_Throw'
    },
    timerIntermediateCatchEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    escalationIntermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    linkIntermediateCatchEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    linkIntermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    compensationIntermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    signalIntermediateCatchEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },
    signalIntermediateThrowEvent: {
      java: 'org.uengine.kernel.bpmn.Event',
      javaView: 'org.uengine.kernel.bpmn.view.EventView',
      shapeId: 'OG.shape.bpmn.E_Intermediate'
    },

    /**
     * End Event
     */
    endEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },
    escalationEndEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },
    errorEndEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },
    compensationEndEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },
    signalEndEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },
    terminateEndEvent: {
      java: 'org.uengine.kernel.bpmn.EndEvent',
      javaView: 'org.uengine.kernel.bpmn.view.EndEventView',
      shapeId: 'OG.shape.bpmn.E_End'
    },

    /**
     * Gateway
     */
    gateway: {
      java: 'org.uengine.kernel.bpmn.Gateway',
      javaView: 'org.uengine.kernel.bpmn.view.GatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    parallelGateway: {
      java: 'org.uengine.kernel.bpmn.ParallelGateway',
      javaView: 'org.uengine.kernel.bpmn.view.ParallelGatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    exclusiveGateway: {
      java: 'org.uengine.kernel.bpmn.ExclusiveGateway',
      javaView: 'org.uengine.kernel.bpmn.view.GatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    inclusiveGateway: {
      java: 'org.uengine.kernel.bpmn.InclusiveGateway',
      javaView: 'org.uengine.kernel.bpmn.view.InclusiveGatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    complexGateway: {
      java: 'org.uengine.kernel.bpmn.Gateway',
      javaView: 'org.uengine.kernel.bpmn.view.GatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    eventBasedGateway: {
      java: 'org.uengine.kernel.bpmn.Gateway',
      javaView: 'org.uengine.kernel.bpmn.view.GatewayView',
      shapeId: 'OG.shape.bpmn.G_Gateway'
    },
    /**
     * Task
     */
    task: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    sendTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    receiveTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    userTask: {
      java: 'org.uengine.kernel.bpmn.HumanActivity',
      javaView: 'org.uengine.kernel.bpmn.view.HumanActivityView',
      shapeId: 'OG.shape.bpmn.A_HumanTask'
    },
    manualTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    businessTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    serviceTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    scriptTask: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    callActivity: {
      java: 'org.uengine.kernel.bpmn.DefaultActivity',
      javaView: 'org.uengine.kernel.bpmn.view.DefaultActivityView',
      shapeId: 'OG.shape.bpmn.A_Task'
    },
    /**
     * Sub Process
     */
    subProcess: {
      java: 'org.uengine.kernel.bpmn.SubProcess',
      javaView: 'org.uengine.kernel.bpmn.view.SubProcessView',
      shapeId: 'OG.shape.bpmn.A_Subprocess'
    },
    transaction: {
      java: 'org.uengine.kernel.bpmn.SubProcess',
      javaView: 'org.uengine.kernel.bpmn.view.SubProcessView',
      shapeId: 'OG.shape.bpmn.A_Subprocess'
    },
    eventSubProcess: {
      java: 'org.uengine.kernel.bpmn.SubProcess',
      javaView: 'org.uengine.kernel.bpmn.view.SubProcessView',
      shapeId: 'OG.shape.bpmn.A_Subprocess'
    },
    /**
     * Data
     */
    dataObject: {
      java: 'org.uengine.kernel.bpmn.DataStore',
      javaView: 'org.uengine.kernel.bpmn.view.DataStoreActivityView',
      shapeId: 'OG.shape.bpmn.D_Data'
    },
    dataStore: {
      java: 'org.uengine.kernel.bpmn.DataStore',
      javaView: 'org.uengine.kernel.bpmn.view.DataStoreActivityView',
      shapeId: 'OG.shape.bpmn.D_Store'
    },
    /**
     * Role
     */
    role: {
      javaView: 'org.uengine.kernel.view.RoleView',
      shapeId: 'OG.shape.HorizontalLaneShape'
    }
  }
};
TypedJsonAdaptor.prototype = {

  getPrevEdgeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['targetRef'] == id) {
        ids.push(flow['sourceRef'] + '-' + flow['targetRef']);
      }
    });
    return ids;
  },
  getNextEdgeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['sourceRef'] == id) {
        ids.push(flow['sourceRef'] + '-' + flow['targetRef']);
      }
    });
    return ids;
  },
  getPrevShapeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['targetRef'] == id) {
        ids.push(flow['sourceRef']);
      }
    });
    return ids;
  },
  getNextShapeIds: function (sequenceFlows, id) {
    var ids = [];
    $.each(sequenceFlows, function (i, flow) {
      if (flow['sourceRef'] == id) {
        ids.push(flow['targetRef']);
      }
    });
    return ids;
  },

  importData: function (data) {
    var me = this;
    var definition = data.definition;
    var canvasViewPort = definition['canvasViewPort'];
    var canvasWidth, canvasHeight, scale;
    if (canvasViewPort) {
      canvasWidth = canvasViewPort['canvasWidth'] ? canvasViewPort['canvasWidth'] : 2000;
      canvasHeight = canvasViewPort['canvasHeight'] ? canvasViewPort['canvasHeight'] : 1800;
      scale = canvasViewPort['scale'] ? canvasViewPort['scale'] : 1;
    } else {
      canvasWidth = 2000;
      canvasHeight = 1800;
      scale = 1;
    }

    var json = {
      opengraph: {
        '@width': canvasWidth,
        '@height': canvasHeight,
        '@scale': scale,
        cell: []
      }
    };

    //childActivities, roles ,sequenceFlows  를 제외한 데이터는 캔버스 'properties' 데이터로 등록.
    var canvasData = {
      properties: {}
    };
    for (var key in data) {
      if (key != 'childActivities' && key != 'roles' && key != 'sequenceFlows') {
        canvasData.properties[key] = data[key];
      }
    }
    json.opengraph['@data'] = escape(OG.JSON.encode(canvasData));

    var makeChild = function (items, childType) {
      console.log(items);
      $.each(items, function (i, item) {
          let view;
          if (childType == 'shape') {
            view = item['elementView'];
          }
          else if (childType == 'sequence') {
            view = item['relationView'];
          }
          else if (childType == 'role') {
            view = item['elementView'];
          }

          var cell = {};
          var tempShape = eval('new ' + view.shapeId + '()');
          if (!tempShape) {
            return;
          }


          if (view.parent) {
            cell['@parent'] = view.parent;
          }
          cell['@shapeType'] = tempShape.TYPE;
          cell['@shapeId'] = view.shapeId;
          cell['@x'] = view.x;
          cell['@y'] = view.y;
          cell['@width'] = view.width;
          cell['@height'] = view.height;

          if (childType == 'shape' || childType == 'role') {
            cell['@id'] = view.id;

            var prevShapeIds = me.getPrevShapeIds(sequenceFlows, view.id);
            var nextShapeIds = me.getNextShapeIds(sequenceFlows, view.id);
            if (prevShapeIds.length > 0) {
              cell['@from'] = prevShapeIds.toString();
            }
            if (nextShapeIds.length > 0) {
              cell['@to'] = nextShapeIds.toString();
            }

            var prevEdgeIds = me.getPrevEdgeIds(sequenceFlows, view.id);
            var nextEdgeIds = me.getNextEdgeIds(sequenceFlows, view.id);
            if (prevEdgeIds.length > 0) {
              cell['@fromEdge'] = prevEdgeIds.toString();
            }
            if (nextEdgeIds.length > 0) {
              cell['@toEdge'] = nextEdgeIds.toString();
            }
          }
          else if (childType == 'sequence') {
            cell['@id'] = item['sourceRef'] + '-' + item['targetRef'];

            if (view.from) {
              cell['@from'] = view.from;
            }
            if (view.to) {
              cell['@to'] = view.to;
            }
          }

          if (view.label) {
            cell['@label'] = view.label;
          }
          if (view.fromLabel) {
            cell['@fromLabel'] = view.fromLabel;
          }
          if (view.toLabel) {
            cell['@toLabel'] = view.toLabel;
          }
          if (view.angle && view.angle !== 0) {
            cell['@angle'] = view.angle;
          }
          if (view.value) {
            cell['@value'] = view.value;
          }
          if (view.style) {
            cell['@style'] = view.style;
          }
          if (view.geom) {
            cell['@geom'] = view.geom;
          }

          if (view.LoopType) {
            cell['@loopType'] = view.LoopType;
          }
          if (view.TaskType) {
            cell['@taskType'] = view.TaskType;
          }
          if (view.exceptionType) {
            cell['@exceptionType'] = view.exceptionType;
          }

          cell['@childs'] = [];

          //elementView , relationView 를 제외한 값을 'properties' 데이터로.
          var shapeData = {
            properties: {}
          };
          if (childType == 'shape' || childType == 'role') {
            for (var prop in item) {
              if (prop != 'elementView') {
                shapeData.properties[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(shapeData));
          }
          else {
            for (var prop in item) {
              if (prop != 'relationView') {
                shapeData.properties[prop] = item[prop];
              }
            }
            cell['@data'] = escape(OG.JSON.encode(shapeData));
          }

          json.opengraph.cell.push(cell);
        }
      );
    };

    var roles = definition['roles'];
    var childActivities = definition['childActivities'][1];
    var sequenceFlows = definition['sequenceFlows'];
    makeChild(roles, 'role');
    makeChild(childActivities, 'shape');
    makeChild(sequenceFlows, 'sequence');
    //childShape(rootGroup, true);
    return json;

  },
  exportData: function (json) {
    var canvasWidth, canvasHeight, rootGroup, canvasScale,
      minX = Number.MAX_VALUE, minY = Number.MAX_VALUE, maxX = Number.MIN_VALUE, maxY = Number.MIN_VALUE,
      i, cell, shape, id, parent, shapeType, shapeId, x, y, width, height, style, geom, from, to,
      fromEdge, toEdge, label, fromLabel, toLabel, angle, value, data, dataExt, element, loopType, taskType, swimlane, textList;

    canvasWidth = json.opengraph['@width'];
    canvasHeight = json.opengraph['@height'];
    canvasScale = json.opengraph['@scale'];
    data = {
      definition: {
        _type: 'org.uengine.kernel.ProcessDefinition',
        canvasViewPort: {
          _type: 'java.util.LinkedHashMap',
          canvasWidth: canvasWidth,
          canvasHeight: canvasHeight,
          scale: canvasScale
        },
        childActivities: [
          'java.util.ArrayList',
          []
        ],
        roles: [],
        sequenceFlows: []
      }
    };

    //데피니션 데이터 원복
    var canvasData = json.opengraph['@data'];
    if (canvasData) {
      canvasData = OG.JSON.decode(unescape(canvasData));
      for (var key in canvasData.properties) {
        data.definition[key] = canvasData[key];
      }
    }
    cell = json.opengraph.cell;
    var totalCount = cell.length;
    var cellCount = 0;

    for (var i = 0, leni = cell.length; i < leni; i++) {
      id = cell[i]['@id'];
      parent = cell[i]['@parent'];
      swimlane = cell[i]['@swimlane'];
      shapeType = cell[i]['@shapeType'];
      shapeId = cell[i]['@shapeId'];
      x = parseInt(cell[i]['@x'], 10);
      y = parseInt(cell[i]['@y'], 10);
      width = parseInt(cell[i]['@width'], 10);
      height = parseInt(cell[i]['@height'], 10);
      style = unescape(cell[i]['@style']);
      geom = unescape(cell[i]['@geom']);

      from = cell[i]['@from'];
      to = cell[i]['@to'];
      fromEdge = cell[i]['@fromEdge'];
      toEdge = cell[i]['@toEdge'];
      label = cell[i]['@label'];
      fromLabel = cell[i]['@fromLabel'];
      toLabel = cell[i]['@toLabel'];
      angle = cell[i]['@angle'];
      value = cell[i]['@value'];
      data = cell[i]['@data'];
      textList = cell[i]['@textList'];
      dataExt = cell[i]['@dataExt'];
      loopType = cell[i]['@loopType'];
      taskType = cell[i]['@taskType'];

      label = label ? unescape(label) : label;

      var object = {};
      //
      //
      // if (view.parent) {
      //   cell['@parent'] = view.parent;
      // }
      // cell['@shapeType'] = tempShape.TYPE;
      // cell['@shapeId'] = view.shapeId;
      // cell['@x'] = view.x;
      // cell['@y'] = view.y;
      // cell['@width'] = view.width;
      // cell['@height'] = view.height;
      //
      // if (childType == 'shape' || childType == 'role') {
      //   cell['@id'] = view.id;
      //
      //   var prevShapeIds = me.getPrevShapeIds(sequenceFlows, view.id);
      //   var nextShapeIds = me.getNextShapeIds(sequenceFlows, view.id);
      //   if (prevShapeIds.length > 0) {
      //     cell['@from'] = prevShapeIds.toString();
      //   }
      //   if (nextShapeIds.length > 0) {
      //     cell['@to'] = nextShapeIds.toString();
      //   }
      //
      //   var prevEdgeIds = me.getPrevEdgeIds(sequenceFlows, view.id);
      //   var nextEdgeIds = me.getNextEdgeIds(sequenceFlows, view.id);
      //   if (prevEdgeIds.length > 0) {
      //     cell['@fromEdge'] = prevEdgeIds.toString();
      //   }
      //   if (nextEdgeIds.length > 0) {
      //     cell['@toEdge'] = nextEdgeIds.toString();
      //   }
      // }
      // else if (childType == 'sequence') {
      //   cell['@id'] = item['sourceRef'] + '-' + item['targetRef'];
      //
      //   if (view.from) {
      //     cell['@from'] = view.from;
      //   }
      //   if (view.to) {
      //     cell['@to'] = view.to;
      //   }
      // }
      //
      // if (view.label) {
      //   cell['@label'] = view.label;
      // }
      // if (view.fromLabel) {
      //   cell['@fromLabel'] = view.fromLabel;
      // }
      // if (view.toLabel) {
      //   cell['@toLabel'] = view.toLabel;
      // }
      // if (view.angle && view.angle !== 0) {
      //   cell['@angle'] = view.angle;
      // }
      // if (view.value) {
      //   cell['@value'] = view.value;
      // }
      // if (view.style) {
      //   cell['@style'] = view.style;
      // }
      // if (view.geom) {
      //   cell['@geom'] = view.geom;
      // }
      //
      // if (view.LoopType) {
      //   cell['@loopType'] = view.LoopType;
      // }
      // if (view.TaskType) {
      //   cell['@taskType'] = view.TaskType;
      // }
      // if (view.exceptionType) {
      //   cell['@exceptionType'] = view.exceptionType;
      // }
      //
      // cell['@childs'] = [];
      //
      // //elementView , relationView 를 제외한 값을 'properties' 데이터로.
      // var shapeData = {
      //   properties: {}
      // };
      // if (childType == 'shape' || childType == 'role') {
      //   for (var prop in item) {
      //     if (prop != 'elementView') {
      //       shapeData.properties[prop] = item[prop];
      //     }
      //   }
      //   cell['@data'] = escape(OG.JSON.encode(shapeData));
      // }
      // else {
      //   for (var prop in item) {
      //     if (prop != 'relationView') {
      //       shapeData.properties[prop] = item[prop];
      //     }
      //   }
      //   cell['@data'] = escape(OG.JSON.encode(shapeData));
      // }
      //
      //
      // //
      // // minX = (minX > (x - width / 2)) ? (x - width / 2) : minX;
      // // minY = (minY > (y - height / 2)) ? (y - height / 2) : minY;
      // // maxX = (maxX < (x + width / 2)) ? (x + width / 2) : maxX;
      // // maxY = (maxY < (y + height / 2)) ? (y + height / 2) : maxY;
      //
      // switch (shapeType) {
      //   case OG.Constants.SHAPE_TYPE.GEOM:
      //   case OG.Constants.SHAPE_TYPE.GROUP:
      //     shape = eval('new ' + shapeId + '()');
      //     if (label) {
      //       shape.label = label;
      //     }
      //     if (data) {
      //       shape.data = OG.JSON.decode(unescape(data));
      //     }
      //     if (textList) {
      //       shape.textList = OG.JSON.decode(unescape(textList));
      //     }
      //     element = this.drawShape([x, y], shape, [width, height], OG.JSON.decode(style), id, parent);
      //     if (element.shape instanceof OG.shape.bpmn.A_Task) {
      //       element.shape.LoopType = loopType;
      //       element.shape.TaskType = taskType;
      //     }
      //     break;
      //   case OG.Constants.SHAPE_TYPE.EDGE:
      //     var list = JSON.parse('[' + value + ']');
      //     var fromto = JSON.stringify(list[0]) + ',' + JSON.stringify(list[list.length - 1]);
      //     shape = eval('new ' + shapeId + '(' + fromto + ')');
      //     if (label) {
      //       shape.label = label;
      //     }
      //     if (data) {
      //       shape.data = OG.JSON.decode(unescape(data));
      //     }
      //     if (textList) {
      //       shape.textList = OG.JSON.decode(unescape(textList));
      //     }
      //     if (fromLabel) {
      //       shape.fromLabel = unescape(fromLabel);
      //     }
      //     if (toLabel) {
      //       shape.toLabel = unescape(toLabel);
      //     }
      //     if (geom) {
      //       geom = OG.JSON.decode(geom);
      //       if (geom.type === OG.Constants.GEOM_NAME[OG.Constants.GEOM_TYPE.POLYLINE]) {
      //         geom = new OG.geometry.PolyLine(geom.vertices);
      //         shape.geom = geom;
      //       } else if (geom.type === OG.Constants.GEOM_NAME[OG.Constants.GEOM_TYPE.CURVE]) {
      //         geom = new OG.geometry.Curve(geom.controlPoints);
      //         shape.geom = geom;
      //       }
      //     }
      //     element = this.drawShape(null, shape, null, OG.JSON.decode(style), id, parent);
      //     break;
      //   case OG.Constants.SHAPE_TYPE.HTML:
      //     shape = eval('new ' + shapeId + '()');
      //     if (value) {
      //       shape.html = unescape(value);
      //     }
      //     if (label) {
      //       shape.label = label;
      //     }
      //     if (data) {
      //       shape.data = OG.JSON.decode(unescape(data));
      //     }
      //     if (textList) {
      //       shape.textList = OG.JSON.decode(unescape(textList));
      //     }
      //     element = this.drawShape([x, y], shape, [width, height, angle], OG.JSON.decode(style), id, parent);
      //     break;
      //   case OG.Constants.SHAPE_TYPE.IMAGE:
      //     shape = eval('new ' + shapeId + '(\'' + value + '\')');
      //     if (label) {
      //       shape.label = label;
      //     }
      //     if (data) {
      //       shape.data = OG.JSON.decode(unescape(data));
      //     }
      //     if (textList) {
      //       shape.textList = OG.JSON.decode(unescape(textList));
      //     }
      //     element = this.drawShape([x, y], shape, [width, height, angle], OG.JSON.decode(style), id, parent);
      //     break;
      //   case OG.Constants.SHAPE_TYPE.TEXT:
      //     shape = eval('new ' + shapeId + '()');
      //     if (value) {
      //       shape.text = unescape(value);
      //     }
      //     if (data) {
      //       shape.data = OG.JSON.decode(unescape(data));
      //     }
      //     if (textList) {
      //       shape.textList = OG.JSON.decode(unescape(textList));
      //     }
      //     element = this.drawShape([x, y], shape, [width, height, angle], OG.JSON.decode(style), id, parent);
      //     break;
      // }
      //
      // if (from) {
      //   $(element).attr('_from', from);
      // }
      // if (to) {
      //   $(element).attr('_to', to);
      // }
      // if (fromEdge) {
      //   $(element).attr('_fromedge', fromEdge);
      // }
      // if (toEdge) {
      //   $(element).attr('_toedge', toEdge);
      // }
      //
      // if (data) {
      //   element.data = OG.JSON.decode(unescape(data));
      // }
      // if (dataExt) {
      //   element.dataExt = OG.JSON.decode(unescape(dataExt));
      // }
      //
      // cellCount++;
      // $(renderer._PAPER.canvas).trigger('loading', [Math.round((cellCount / totalCount) * 100)]);
    }

    return this.json;
  }
}
;
TypedJsonAdaptor.prototype.constructor = TypedJsonAdaptor;
