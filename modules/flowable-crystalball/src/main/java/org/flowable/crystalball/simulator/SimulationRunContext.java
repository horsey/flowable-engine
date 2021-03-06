/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.crystalball.simulator;

import java.util.Stack;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.runtime.Clock;
import org.flowable.variable.service.delegate.VariableScope;

/**
 * Context in which simulation is run. It contains references to process engine, event calendar.
 *
 * @author martin.grofcik
 */
public abstract class SimulationRunContext {

    //
    // Process engine on which simulation will be executed
    //
    protected static ThreadLocal<Stack<ProcessEngine>> processEngineThreadLocal = new ThreadLocal<>();

    //
    // Simulation objects
    //
    protected static ThreadLocal<Stack<EventCalendar>> eventCalendarThreadLocal = new ThreadLocal<>();

    //
    // Simulation run Id
    //
    protected static ThreadLocal<Stack<String>> simulationRunIdThreadLocal = new ThreadLocal<>();

    //
    // Variable scope used for getting/setting variables to the simulationManager
    //
    protected static ThreadLocal<Stack<VariableScope>> executionThreadLocal = new ThreadLocal<>();

    public static RuntimeService getRuntimeService() {
        Stack<ProcessEngine> stack = getStack(processEngineThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek().getRuntimeService();
    }

    public static void setProcessEngine(ProcessEngine processEngine) {
        getStack(processEngineThreadLocal).push(processEngine);
    }

    public static ProcessEngine getProcessEngine() {
        return getStack(processEngineThreadLocal).peek();
    }

    public static void removeProcessEngine() {
        getStack(processEngineThreadLocal).pop();
    }

    public static TaskService getTaskService() {
        Stack<ProcessEngine> stack = getStack(processEngineThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek().getTaskService();
    }

    public static EventCalendar getEventCalendar() {
        Stack<EventCalendar> stack = getStack(eventCalendarThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setEventCalendar(EventCalendar eventCalendar) {
        getStack(eventCalendarThreadLocal).push(eventCalendar);
    }

    public static String getSimulationRunId() {
        Stack<String> stack = getStack(simulationRunIdThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setSimulationRunId(String simulationRunId) {
        getStack(simulationRunIdThreadLocal).push(simulationRunId);
    }

    public static void removeEventCalendar() {
        getStack(eventCalendarThreadLocal).pop();
    }

    public static HistoryService getHistoryService() {
        Stack<ProcessEngine> stack = getStack(processEngineThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek().getHistoryService();
    }

    public static RepositoryService getRepositoryService() {
        Stack<ProcessEngine> stack = getStack(processEngineThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek().getRepositoryService();
    }

    public static VariableScope getExecution() {
        Stack<VariableScope> stack = getStack(executionThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setExecution(VariableScope execution) {
        getStack(executionThreadLocal).push(execution);
    }

    public static Clock getClock() {
        return getProcessEngine().getProcessEngineConfiguration().getClock();
    }

    protected static <T> Stack<T> getStack(ThreadLocal<Stack<T>> threadLocal) {
        Stack<T> stack = threadLocal.get();
        if (stack == null) {
            stack = new Stack<>();
            threadLocal.set(stack);
        }
        return stack;
    }

}
