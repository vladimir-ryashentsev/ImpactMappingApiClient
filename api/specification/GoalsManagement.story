Feature: Goals management

Narrative:
As a goal-oriented person
So that I can reach my goals easer and faster
I want to manage my goals according to impact mapping

Scenario: adding a goal
When I add a goal(title=Подготовить демонстрацию возможностей, date=2016-11-15)
Then the goal(title=Подготовить демонстрацию возможностей, date=2016-11-15) appears in the goals list

Scenario: removing a goal
Given I have the goal(id=a2af48ce-565d-473f-a17e-bc06684cbc57)
When I remove the goal(id=a2af48ce-565d-473f-a17e-bc06684cbc57)
Then the goal(id=a2af48ce-565d-473f-a17e-bc06684cbc57) dissapears from goals list